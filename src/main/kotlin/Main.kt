import java.io.File
import java.io.FileWriter
import java.util.Scanner

fun main(args: Array<String>) {
  println("Starting")
  val listOfFiles = getFileList()
  TagExtractor(listOfFiles)
  val records= matchAndWrite(listOfFiles)
  println("...and done.")
}
//////////////////////////////////////////////////////////////
fun getFileList(): MutableList<String> {
  /*
// Ask the user for the location of the directory.
  println("Please enter the root directory for where your .md files live.\n> ")
  val scanner = Scanner(System.`in`)
  val userInput = scanner.nextLine()
  val baseDir = File(userInput)

   */

  val baseDir = File("/google/src/cloud/dmail/compose-recs-newmd/google3/third_party/devsite/android/en/guide/playcore")

// Declare the name of the file that will store the file list
  val fileListFile =
    File(baseDir.canonicalPath.plus("/md_list.txt"))


  // Get filetree
  val fileTree = baseDir.walkTopDown()

  // Create a string List to store all names of .md files
  val fileList = mutableListOf<String>()

  // Iterate through the filetree and store all filenames with ".md" in a String array
  for (file in fileTree) {
    if (!file.name.contains(".md")) continue
    if (file.name.startsWith("_")) continue
    fileList.add(file.canonicalPath)
  }

// Write the list of md_files to a text file.
  val writer = FileWriter(fileListFile)
  for (file in fileList)
    writer.write(file + "\n")
  writer.close()
  return fileList
}

/////////////////////////////////////////////
fun TagExtractor (listOfFiles: MutableList<String>)
{
  // Iterate through the filetree
  for (file in listOfFiles) {
    val finalResult = mutableListOf<String>()
    val result = mutableListOf<Char>()
    var counter = 0

    // Read the content of each file into a big String
    val testFileContent = File(file).readText()

    // See if the next three characters are consecutive backticks. Start
    // by reading them into three variables.
    for (i in 0..testFileContent.length - 3) {
      var currentChar = testFileContent[i]
      var nextChar = testFileContent[i + 1]
      var nextNextChar = testFileContent[i + 2]

      // Skip over code blocks (denoted by three backticks)
      // This only skips the first backtick, but then the subsequent two
      // end up being a pair containing nothing. The subsequent if statement
      // skips over pairs of backticks with nothing between them.
      if (currentChar == '`' && nextChar == '`' && nextNextChar == '`')
        continue

      if (currentChar == '`') {
        counter++
      }
      if (counter == 1 && currentChar != '`')
        result.add(testFileContent[i])
      if (counter == 2) {
        result.add(' ')
        counter = 0
      }
    }
    val resultAsString = StringBuilder()
    for (i in result)
      resultAsString.append(i)
    val finalString = resultAsString.toString()
    val finalStringSplit = finalString.split(" ")

    val uniqueWords=finalStringSplit.toSet()



    for (word in uniqueWords)
      if (word.isNotEmpty())
        finalResult.add(word)
    finalResult.sort()
    val outputFileName=file+"-tag"
    val writer=FileWriter(file.plus("-tag"))
    for (word in finalResult)
    {
      writer.write(word+"\n")
    }
    writer.close()


    println(file+"\n")
    println(finalResult+"\n\n")
  }


}
///////////////////////////////////////////////////////////////////
fun matchAndWrite(listOfFiles:MutableList<String>) {

  val records = mutableListOf<String>()

  var counter1 = -1
  for (fileName in listOfFiles) {
    counter1++
    val file1 = File(fileName + "-tag")
    var counter2 = -1

    for (fileName2 in listOfFiles) {
      counter2++
      if (counter1 >= counter2)
        continue
      val file2 = File(fileName2 + "-tag")
      val content1 = file1.readText()
      val content2 = file2.readText()
      if ((content1.isEmpty()) || (content2.isEmpty()))
        continue
      val result = content1.equals(content2)
      if (result) {
        records.add(file1.name)
        records.add(file2.name)
        println()
      }
    }
  }
println()
}