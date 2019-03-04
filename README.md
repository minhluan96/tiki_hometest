# tiki_hometest

Implement a simple excel application.

Input: a file `testcases.txt` with the following structure:

 - The first line will contain a number N indicating the number of cells
 - The rest 2*N lines will have the following structure:
 
    - The first line contains the cell name (e.g A1)
    - The second line contains the cell content
    
The content of each cell can be a number (double), or a mathematic formula (consists of +-*/) and the formula can also refers to other cells.

Output: the final values of each cells, sorted alphabetically by the cell names

Example

Input:
3
A1
5
A2
A1 * 5 + B1
B1
6


Output:
A1
5
A2
31
B1
6


Notes:
  - The application must strictly read from the specified file
  - The application must report for any circular dependencies
  
Example:

Input:
2
A1
A2 * 2
A2
A1 + 5


Output:
Circular
dependency
between A1 and
A2 detected


Follow up question:
- Re-calculate the cells when the value of one cell changes
