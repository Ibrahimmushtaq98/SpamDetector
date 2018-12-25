Ibrahim Mushtaq

# Spam Detector

This file contains the Assignment 1 Stuff

## How To Stuff

First Download the full repo through download as zip or through command
```
git clone https://github.com/Ibrahimmushtaq98/csci2020u.git
```

Note, you may want to use intellij for running this program, if not, there are other ways to run this program

### Intellij Version

Open Intellij and select the Assignment 1 Folder, and press the green play button

### Command Line

You would first want to access the directory
```
cd Assignment/Assignment\ 1/src
```
then

```
javac main.java && java main
```

### Jar file

Ive created a executable file that you can run. The jar file can be found within this directory

```
Assignment/Assignment 1/out/artifacts
```

### Enhancement

For making the program work much better, Ive research a way called N-Gram, which basically takes a string
like "Lets go to the car wash" and depending what n is, for example n=2, then the output would be "Lets go", 
"go to", "to the", "the car", "car wash". This may seems redundant but it make sense. Lets take the word
"buy", "buy" could be interpret as both spam and ham. We just don't have the enough
info to make a decision. A solution would be to look at the next word. The next word could be "Viagra". Due
to the next word, we could make an informative decision. N-Gram accomplish this.

Unfortunately, either my programming is wrong (most probably) or the accuracy is lower than the original program, and
the precision is in the same boat


The N-Gram code can be found in `Filter.java` on line `226` and below   

