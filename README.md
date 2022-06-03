
  
## The Program
BMP_Negatives.scala converts a BMP image to the negative image as a new file.

## Setup
* Install [Scala 3.1.2](https://www.scala-lang.org/download/3.1.2.html)

## Running the Program
To run the program enter `scala BMP_Negatives.scala YourBMPFileEName` on the command line.
* Upon succesful completion, ``Successfly created the negative image`` will be output to the command line. 
* The name of the output file will be the concatination of "Negative" + InputFileName:
	* *Negative*InputFileName


### Input Requirements
* Input file must be a BMP image file
	* Must have 24 bit Bits Per Pixel
	* Cannot be compressed

Note: other file types or corrupted BMP files may cause the program to crash or produce unexpected outputs

## How it Works
The program works by parseing the header of the input BMP file for the location of the pixels in the image itself. It then computes the negative of each pixel and writes the new file to output.