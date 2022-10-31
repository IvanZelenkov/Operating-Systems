# Principles of Operating Systems

## Assignment 3

### Content
- Collector
    - Assignment3.pdf
    - pom.xml
    - src
        - main
            - java
               - CollectorTest
                 - CallableTask.java
                 - Collector.java
               - PartA
                   - outputScreenshots
                       - MultiThreadedCollectorOutput.png
                       - SingleThreadedCollectorOutput.png
                   - MultiThreadedCollector.java
                   - SingleThreadedCollector.java
               - PartB
                   - outputScreenshots
                     - MultiThreadedCollectorOutput.png
                     - SingleThreadedCollectorOutput.png
                   - CallableTask.java
                   - MultiThreadedCollector.java
                   - SingleThreadedCollector.java
               - PartC
                   - Observations (Ivan Zelenkov).pdf
            - resources
                - PartA
                    - SMS_Spam.txt
                - PartB
                    - CarAds.csv
                    - ConsumerComplaints.csv
                    - FakeNews.csv
                    - Hotel_Reviews.csv
                    - IMDB.csv
                    - MoviePlots.csv
                    - NYC_Restaurants.csv
                    - Oscar2019Tweets.csv
                    - Resume.csv
                    - SMS_Spam.txt
                    - TimeUse.csv
                    - Traffic_Violations.csv
                    - UFOReports.txt
                    - WineReviews.csv

### Description
I will be working with 14 different datasets (each file has a different dataset) where the most occurred word in each 
file should be found. There will be implemented single-threaded and multi-threaded versions to solve that problem 
and in conclusion, I will create an observation about programs.

### Instructions and Installations
1. Install [Intellij IDEA](https://www.jetbrains.com/idea/download/#section=mac)
2. Open Intellij, go to File -> Project Structure, and change a project SDK to the SDK that is installed on the machine.
3. Open the pom.xml file, and change <maven.compiler.source> to the version of SDK that is on your machine. For example,

        <properties>
            <maven.compiler.source>17</maven.compiler.source>
            <maven.compiler.target>17</maven.compiler.target>
        </properties>

4. Rebuild the project.
5. Download the [resources](https://drive.google.com/drive/folders/17mwn-tgtAs_CDkTlUAVRvWX5kbGZ3rzh?usp=share_link) folder 
and put it in the main folder of the current project.
6. Now you can run .java files in Intellij IDEA.

### Support
*itproger.ivan@gmail.com*

### Author
*Ivan Zelenkov*
