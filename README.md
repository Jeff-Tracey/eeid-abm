# eeid-abm

Program written by Jeff A. Tracey, PhD (jeff.a.tracey@proton.me)

Java code to accompany the article:

Tracey, Jeff A., et al. "An agent‐based movement model to assess the impact of landscape fragmentation on disease transmission." Ecosphere 5.9 (2014): 1-24.   https://doi.org/10.1890/ES13-00376.1


**To compile the Java program:**

From a BASH terminal, cd into the src directory, and then:
```bash
javac -d ../classes com/eid/app/diseasesim/DiseaseSim01.java
cd ../classes
jar -cvmf manifest.txt DiseaseSim.jar com
```

**To run the simulation:**

The command line arguments are two numbers indicating the starting and ending parameter scenario numbers from 0 to 4199; for example: `java -jar DiseaseSim.jar 0 2`

NOTE: a directory named `output` must be present in the directory containing `DiseaseSim.jar`.

