# Principles of Operating Systems

## Assignment 4

### Content
- Collector
    - Assignment4.pdf
    - pom.xml
    - src
        - main
            - java
                - DeadlockDetector.java
                - RAG.java
                - Vertex.java
            - resources
                - input
                    - input1.txt
                    - input2.txt
                    - input3.txt
                - output
                    - output1.txt
                    - output2.txt
                    - output3.txt
            - combined_version
                - Deadlock.java
                - README.md
                - input1.txt
                - input2.txt
                - input3.txt
                - output1.txt
                - output2.txt
                - output3.txt

### Description
There are a variety of approaches to dealing with resource deadlocks. For this assignment,
I will investigate deadlock detection. The approach to deadlock detection that was discussed
in class is to model resource allocations and requests with a Resource Allocation Graph (RAG). 
Deadlock detection is then accomplished by finding a cycle in the graph.

### Instructions and Installations
1. Install [Intellij IDEA](https://www.jetbrains.com/idea/download/#section=mac)
2. Open Intellij, go to File -> Project Structure, and change a project SDK to the SDK that is installed on the machine.
3. Open the pom.xml file, and change <maven.compiler.source> to the version of SDK that is on your machine. For example,

        <properties>
            <maven.compiler.source>17</maven.compiler.source>
            <maven.compiler.target>17</maven.compiler.target>
        </properties>

4. Rebuild the project.
5. Now you can run .java files in Intellij IDEA.
6. Enter a filename to read.
7. Enter a filename where the output will be saved.
8. Check the console output and the file where it is saved.
9. Now, you can then go ahead and test more input files if you have.

### Support
*itproger.ivan@gmail.com*

### Author
*Ivan Zelenkov*
