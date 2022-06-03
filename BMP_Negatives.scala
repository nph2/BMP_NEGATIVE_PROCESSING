import java.nio.file.{Files, Paths}
import java.nio.{ByteBuffer, ByteOrder}
import java.io.{BufferedOutputStream, FileOutputStream}

object BMP_Negatives{

    // instance variables
    
    // corseponding locations in the BMP header
    val imageLocation = 10
    val widthLocation = 18
    val heightLocation = 22
    val pixLocation = 28

    // offset header length
    val hLength = 4

    // used to compute padded width
    val divisor = 32

    // assumed pixel size
    val pixSize = 24

    // max value of an 8 bit signed integer 
    val maxNum = 127

    def parseArgs(args: Array[String]) = {

        // length check
        if (args.length != 1){
            Console.err.println(s" ${args.length} Invalid number of arguments")
            sys.exit(1)
        }
        
        // help statement check
        if(args(0) == "-h" || args(0) == "--help"){
            Console.err.println("BMP_Negatives.scala | converts a BMP image to the negative image as a new file")
            Console.err.println("usage: BMP_Negatives.scala BMPFILENAME")
            sys.exit(1)     
        }

        // File Exists Check
        val filePath = Paths.get(args(0))
        if(!(Files.exists(filePath))){
            Console.err.println("File Does not exist")
            sys.exit(1)
        }

    }

    // converts byte array into an integer
    def getIntValue(image: Array[Byte], offset: Int, length: Int): Int = {
        if ( length < 4 ){
            return ByteBuffer.wrap(image, offset, length).order(ByteOrder.LITTLE_ENDIAN).getShort.toInt
        }
        return ByteBuffer.wrap(image, offset, length).order(ByteOrder.LITTLE_ENDIAN).getInt
    }
    
    // Main Function
    def main(args: Array[String]) =
    {
        // parse arguments
        parseArgs(args)
       
        // read file from input
        val filePath = Paths.get(args(0))
        val inFile = Files.readAllBytes(filePath)

        // Compute the pixel size. Note this includes compression, but since, there is an assumption of no compression it is 0. 
        val actualPixSize = getIntValue(inFile, pixLocation, hLength)
        if(actualPixSize != pixSize){
            // if compression is used the value will be greater than 128 due to Little Endian
            if(actualPixSize > maxNum ){
                Console.err.println("The image uses compression. Compression is not supported.")
            } else {
                 Console.err.println(s"Pixel size is not compatible. The pixel size is ${actualPixSize}. \nHowever, ${pixSize} is the only supported  pixel size")
            }
            sys.exit(1)
        }

        // compute the location and dimensions of the image in the BMP file
        val offset = getIntValue(inFile, imageLocation, hLength)
        val height = getIntValue(inFile, heightLocation, hLength)
        val notPaddedWidth = getIntValue(inFile, widthLocation, hLength).toDouble
        val width = hLength * math.ceil(pixSize * notPaddedWidth / divisor).toInt
       
        // create a new file as output
        val outFile = new BufferedOutputStream(new FileOutputStream("Negative_"+args(0)))
        outFile.write(inFile.slice(0, offset))
       
        // copy the negative of the image
        try{
            val image = inFile.slice(offset, offset+ height*width)
            outFile.write(image.map(x => (~x).toByte))      
     
        } catch{ 
            case x => {
                Console.err.println("There was an error. Input file must be a BMP file that is not corrupted.")
                sys.exit(1)
            }
        }
        finally {
            outFile.close
        }
        println("Successfly created the negative image")
        sys.exit(0)
    }
}
