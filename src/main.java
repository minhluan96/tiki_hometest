import java.io.*;
import java.util.*;

public class main {

    public static void main(String args[] ) {

        List<String> listLine = new ArrayList<>();
        int size = 0;

        Scanner s = new Scanner(System.in);
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader("testcases.txt"));
            String line = br.readLine();
            int i = 0;
            while(line != null) {

                if (i == 0) {
                    size = Integer.parseInt(line);
                } else {
                    listLine.add(line);
                }
                i++;
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        Map<String, String> map = new HashMap<>();
        Map<String, String> clone = new HashMap<>();
        Map<String, String> detectedMap = new HashMap<>();

        int i = 0;
        while (i < size * 2) {
            map.put(listLine.get(i), listLine.get(i + 1));
            clone.put(listLine.get(i), listLine.get(i + 1));
            i+=2;
        }

        map = sortByValue(map);



        boolean isCorrect = isCalculable(map, detectedMap);
        if (!isCorrect) {
            Map.Entry<String,String> entry = detectedMap.entrySet().iterator().next();
            String data = "Circular dependency between " + entry.getKey() + " and " + entry.getValue() + " detected";
            System.out.println(data);
            exportFile("output.txt", data);
        } else {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String str = entry.getValue();
                String key = entry.getKey();
                String[] subStr = seperateList(str);
                List<String> formattedStr = convertToPostFix(subStr);
                double calculatedValue = calculate(formattedStr);
                clone.put(key, calculatedValue + "");
            }

            clone = sortByKey(clone);
            String data = "";
            for (Map.Entry<String, String> entry : clone.entrySet()) {
                String value = entry.getValue();
                String key = entry.getKey();
                System.out.println(key + "\n" + value);
                data += key + "\n" + value + "\n";
            }
            exportFile("output.txt", data);
        }


    }

    private static void exportFile(String fileName, String data) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(new File(fileName));
            os.write(data.getBytes(), 0, data.length());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static Map<String, String> sortByValue(Map<String, String> unsortMap) {


        List<Map.Entry<String, String>> list =
                new LinkedList<Map.Entry<String, String>>(unsortMap.entrySet());


        Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
            public int compare(Map.Entry<String, String> o1,
                               Map.Entry<String, String> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<String, String> sortedMap = new LinkedHashMap<String, String>();
        for (Map.Entry<String, String> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }


    private static Map<String, String> sortByKey(Map<String, String> unsortMap) {


        List<Map.Entry<String, String>> list =
                new LinkedList<Map.Entry<String, String>>(unsortMap.entrySet());


        Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
            public int compare(Map.Entry<String, String> o1,
                               Map.Entry<String, String> o2) {
                return (o1.getKey()).compareTo(o2.getKey());
            }
        });

        Map<String, String> sortedMap = new LinkedHashMap<String, String>();
        for (Map.Entry<String, String> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    private static boolean isCalculable(Map<String, String> map, Map<String, String> clone) {

        Map<String, Double> mapDouble = new HashMap<>();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (isNumeric(entry.getValue())) {
                mapDouble.put(entry.getKey(), Double.parseDouble(entry.getValue()));
            }
        }


        for (String key : mapDouble.keySet()) {
            if (map.containsKey(key)) {
                map.remove(key);
            }
        }

        List<String> stringKeys = new ArrayList<>();


        for (Map.Entry<String, String> entry : map.entrySet()) {
            clone.put(entry.getKey(), entry.getValue());
            stringKeys.add(entry.getKey());
        }


        // replace alphabet with numeric value
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String valueStr = entry.getValue();
            String keyStr = entry.getKey();
            for (String key : mapDouble.keySet()) {
                Double valueNumeric = mapDouble.get(key);
                if (valueStr.contains(key)) {
                    valueStr = valueStr.replace(key, valueNumeric + "");
                    map.put(keyStr, valueStr);
                }
            }
        }

        boolean isCircular = false;

        for (Map.Entry<String, String> entry : clone.entrySet()) {
            for (String strKey : stringKeys) {
                if (entry.getValue().contains(strKey)) {
                    clone.put(entry.getKey(), strKey);
                    isCircular = true;
                }
            }
        }

        if (isCircular) return false;

        return true;
    }

    private static int getPriority(String ope) {
        if (ope.equals("*") || ope.equals("/")) return 2;
        else if (ope.equals("+") || ope.equals("-")) return 1;
        else return 0;
    }


    private static int isOperator(String ope) {
        if (getPriority(ope) == 0)
        {
            if (!ope.equals("(") && !ope.equals(")")) return 0;
            else return 1;
        }
        return 2;
    }

    private static Double calculate(List<String> input) {
        List<String> stack = new ArrayList<>();
        for (int i = 0; i < input.size(); i++)
        {
            if (isOperator(input.get(i)) == 0) {
                stack.add(input.get(i));
            }
            else
            {
                double b = Double.parseDouble(stack.get(stack.size() - 1));
                stack.remove(stack.size() - 1);
                double a = Double.parseDouble(stack.get(stack.size() - 1));
                stack.remove(stack.size() - 1);
                if (input.get(i).equals("+")) {
                    stack.add(a + b + "");
                } else if (input.get(i).equals("-")) {
                    stack.add(a - b + "");
                }
                else if (input.get(i).equals("*")) {
                    stack.add(a * b + "");
                }
                else if (input.get(i).equals("/")) {
                    stack.add(a / b + "");
                }
            }
        }
        return Double.parseDouble(stack.get(stack.size() - 1));
    }

    private static String[] seperateList(String exp) {
        String[] subStr = exp.split(" ");
        return subStr;
    }

    private static List<String> convertToPostFix(String[] exp) {
        List<String> stack = new ArrayList<>();
        List<String> output = new ArrayList<>();

        String number = "";
        for (int i = 0; i < exp.length; i++) {
            String s = exp[i] + "";
            if (isOperator(s) == 0) {
                number += s;

            } else {
                if (number.length() > 0) {
                    output.add(number);
                    number = "";
                }
                if (isOperator(s) == 1) {
                    if (s.equals("(")) {
                        stack.add("(");
                    } else if (s.equals(")")) {
                        String pop = stack.get(stack.size() - 1);
                        stack.remove(pop);
                        while (!pop.equals("(")) {
                            output.add(pop);
                            pop = stack.get(stack.size() - 1);
                            stack.remove(pop);
                        }
                    }

                } else {
                    while (!stack.isEmpty() && getPriority(stack.get(stack.size() -  1)) >= getPriority(s)) {
                        output.add(stack.get(stack.size() - 1));
                        stack.remove(stack.size() - 1);
                    }
                    stack.add(s);
                }
            }


        }

        if (number.length() > 0) {
            output.add(number);
            number = "";
        }
        while (!stack.isEmpty()) {
            output.add(stack.get(stack.size() - 1));
            stack.remove(stack.size() - 1);
        }
        return output;
    }


    private static boolean isNumeric(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


}
