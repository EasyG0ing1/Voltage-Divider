# Voltage Divider

This program makes it easy to calculate any value in the standard voltage divider equation.

Simply click on a resistor or any of the voltage points and when you set any three of the four values, you can calculate the fourth value.

Easily find resistor values to attain a desired output voltage.

Once the equation has been calculated, it posts the result and accumulates a log of all your calculations. The log also includes the voltage across each resistor for convenience.

There is a menu at the upper right of the window. When you hover over it, it lights up and it lets you either clear all of the values, copy the entire log of RAW values to your clipboard in CSV format so that you can paste them into a spreadsheet for further analysis (including header row), or exit the program.

The output values are rounded, voltages to three decimals and resistors to two, but it uses the un-rounded values for calculations.

You can type in resistor values as full raw values or using the SI unit of either K or M (kilo-ohm or mega-ohms upper or lower case) and the program will convert them to raw values and use those values for the calculations.

It's a simple but useful app.

Maven will be able to import all of the necessary libraries, and I included a custom library that I wrote in the root folder that you can import before you run the project. It's called CustomControls.jar

![Screen Shot 2020-12-14 at 4.40.39 AM.png](src/main/resources/images/Screen%20Shot%202020-12-14%20at%204.40.39%20AM.png)

![Screen Shot 2020-12-14 at 11.47.15 AM.png](src/main/resources/images/Screen%20Shot%202020-12-14%20at%2011.47.15%20AM.png)

![Screen Shot 2020-12-14 at 11.54.48 AM.png](src/main/resources/images/Screen%20Shot%202020-12-14%20at%2011.54.48%20AM.png)

![Screen Shot 2020-12-14 at 4.41.12 AM.png](src/main/resources/images/Screen%20Shot%202020-12-14%20at%204.41.12%20AM.png)

![Screen Shot 2020-12-14 at 4.41.27 AM.png](src/main/resources/images/Screen%20Shot%202020-12-14%20at%204.41.27%20AM.png)

![Screen Shot 2020-12-14 at 11.51.19 AM.png](src/main/resources/images/Screen%20Shot%202020-12-14%20at%2011.51.19%20AM.png)

The ComboFinder is a screen where you can just enter a bunch of resistor values that you have, then the program will run all of them through every possible combination and will find the voltage output value at R2 using the input voltage value that you supply. You can then put in a desired output voltage at R2 along with the tolerance that you will accept and then tell it to find all of the value combinations that meet your criteria.

You can enter in your resistor values one at a time, or check the box and just copy and paste a list of values (one value on each line) and set your Vin and desired Vout along with the tolerance and get the list of all combinations that meet your criteria.

![Screen Shot 2020-12-14 at 11.44.38 AM.png](src/main/resources/images/Screen%20Shot%202020-12-14%20at%2011.44.38%20AM.png)

![Screen Shot 2020-12-14 at 11.45.30 AM.png](src/main/resources/images/Screen%20Shot%202020-12-14%20at%2011.45.30%20AM.png)

![Screen Shot 2020-12-14 at 11.45.42 AM.png](src/main/resources/images/Screen%20Shot%202020-12-14%20at%2011.45.42%20AM.png)

And just like on the first form, you can copy those raw values to your clipboard in CSV format so that you can paste them into a spreadsheet for further analysis.


