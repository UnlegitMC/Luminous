# Luminous
A injection minecraft cheat using jvm attach api  
Website: https://lumi.getfdp.today

## Build
We used a thing called ```Wrapper``` to make development easier, so you need to generate the wrappers for your workspace.
```
./gradlew genWrapper
```
while the wrapper has generated, you can use ```./gradlew build``` to build the jar.   
And make sure if the wrapper map is changed, you need to regenerate the wrapper.

## Usage
Run the jar with ```java -jar Luminous.jar``` on console.
And select the JVM you want to attach with the pop-up GUI.  
If you don't want to use pop-up GUI, you can use console to select JVM like this ```java -jar -Dluminous.useconsole Luminous.jar```.  
We used jvm attach api to attach the JVM, so make sure you are using JDK.
