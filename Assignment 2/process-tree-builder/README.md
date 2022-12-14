# Principles of Operating Systems

## Assignment 2

### Content
- process-tree-builder
    - Assignment2.pdf
    - pom.xml
    - src
        - main
            - java
                - GraphvizBuilder.java
                - NaryTree.java
                - Node.java
                - ProcessTreeBuilder.java
            - resources
                - PartA
                    - fork3calls.json
                    - fork6calls.json
                    - fork10calls.json
                    - main.c
                    - main.exe
                - PartC
                    - fork3calls.json
                    - fork6calls.json
                    - fork10calls.json
                    - main.c
                    - main.exe
                - PartD
                    - main.c
                    - main.exe

### Description
For this assignment, I will write an implementation of the N-are tree where parents can have multiple children
and each node represents a process with the following data: PID, PPID, and level. Every child node contains
PPID that should be equal to the parent PID. PartA generates a JSON file with the list of processes based on
the input number of fork() function calls, and PartC is a modification of PartA where the parent waits for
the child to terminate soon after the fork and then breaks from the loop and exits. PartD requires me to implement
inter-process communication (IPC) between a parent and child process.

### Instructions and Installations
For PartA and PartC:
1. Compile main.c file.

       gcc main.c -o main

2. Run executable file.

       ./main

3. Enter the number of calls to the fork() function.
4. Enter a file name where the output will be saved.

For PartB:
1. Install [Intellij IDEA](https://www.jetbrains.com/idea/download/#section=mac)
2. Install [Graphviz](https://graphviz.org/download/) for the appropriate operating system.
3. Open Intellij, go to File -> Project Structure, and change a project SDK to the SDK that is installed on the machine.
4. Open the pom.xml file, and change <maven.compiler.source> to the version of SDK that is on your machine. For example,

        <properties>
            <maven.compiler.source>17</maven.compiler.source>
            <maven.compiler.target>17</maven.compiler.target>
        </properties>

5. Rebuild the project.
6. Run ProcessTreeBuilder.java class in Intellij IDEA.
7. Enter the name of the directory where the processes file is located.
8. Enter a filename to read.
9. In the Desktop folder on your PC, you will see a GraphvizOutput folder with generated images.

For PartD:
1. Enter a chore for the child process.
2. Send a message back to the parent that the child has completed the given chore.
3. The parent should return a confirmation message that the child completed a chore.

### Support
*itproger.ivan@gmail.com*

### Author
*Ivan Zelenkov*
