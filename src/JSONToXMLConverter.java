import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

import org.json.*;



/**
 * at this stage the entire program will basically be a simple script that runs in this class, 
 * traversing the json files and creating a corresponding xml file for each one.
 * @author max
 *
 */
public class JSONToXMLConverter {

	
	public static void main(String[] args){

		doFileWalk(new XmlConverterVisitor());//create the walker that will explore the tree of our json files
	}

	//TODO: ultimately add the starting directory as a prompted string input from the user but for now we sjust hardcode it
	/**
	 * takes a walker and a starting directory and recursively traverses the tree of child directories, converting
	 * json files to xml files
	 * 	 * @param fileVisitor the converter visitor that will make a converted version of every file that it visits
	 * 
	 * 
	 */
	private static void doFileWalk(FileVisitor fileVisitor) {
		//start the file visitor on its journey
		//hardcode the start point path !!!
		Path initPath = FileSystems.getDefault().getPath("C:\\!2015SCHOLARSHIT\\tinyTasmaniaDataSample");
		long startTime = System.currentTimeMillis();
		try {
			System.out.println("starting walk of file tree...");
			Files.walkFileTree(initPath, fileVisitor);
		} catch (IOException e) {
			System.out.println("IO EXCEPTION INCURRED STARTING THE VISITOR'S WALK");
			e.printStackTrace();
		}finally{
			System.out.println("finished walking file tree after approx " + ((System.currentTimeMillis() - startTime)/1000)  + " seconds : )" );
		}
		
	}

	
	
	
	
	private static class XmlConverterVisitor implements FileVisitor{

		
		//INTERFACE BEHAVIOUR METHODS////////////////////////////////////////
		
		@Override
		public FileVisitResult postVisitDirectory(Object arg0, IOException arg1)
				throws IOException {
			//System.out.println("just visited a directory with properties: " + arg0 + " " + arg1);
			return java.nio.file.FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult preVisitDirectory(Object arg0,
				BasicFileAttributes arg1) throws IOException {
			//System.out.println("previsiting a directory with properties: " + arg0 + " " + arg1);
			return java.nio.file.FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(Object arg0, BasicFileAttributes arg1)
				throws IOException {
			
			//System.out.println("about to visit a file with properties: " + arg0 + " " + arg1);
			
			//if the encountered file is a .json file, we should give it to the conversion method to be converted to XML
			if(!(arg0 instanceof Path)){
				throw new RuntimeException("unexpected file object type provided by file visitor. Expected Path object.");
			}
			Path pathToFile = (Path)arg0;
			if(pathToFile.toString().substring(pathToFile.toString().length() - 5, pathToFile.toString().length()).equals(".json")){
				//so we have encountered a file that is purportedly .json, we should attempt to produce an xml file from it
		//	try{
				convertToXml(pathToFile);
		/*	}catch(IOException e){ THIS IS COMMENTED OUT BECAUSE WE ARE TRYING TO FIGURE OUT WHY WRITING THE XML STRING THROW EXCEPTION
				System.out.println("attempted to convert the following file to XML and incurred an io exception: " + pathToFile);
				e.printStackTrace();
			}*/
			}else{
				//System.out.println("non json file encountered: " + arg0);
			}
			return java.nio.file.FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFileFailed(Object arg0, IOException arg1)
				throws IOException {
			throw new RuntimeException("the operation is not catered for atm but it should be fam");
		}
		/////////////////////////////////////////////////////////////////////
		
		//TODO: at the moment this method only hackily checks that the xml string that we will be saving to file is "well formed" by relying upon the call to XML.toString to throw the JSONException (presumably... havent even looked at code). 
		
		/**
		 * attempts to create an xml version of a file that is purported to be in the .json format.
		 * This method will be attempted with every file that is in the directory tree of whatever our start point directory is.
		 * For now, it does not handle poorly formed json gracefully, will throw a runtime exception.
		 * @param pathToFile the path of the json file that is to be converted
		 */
		private void convertToXml(Path pathToFile) throws IOException {
			//System.out.println("about to convert this file to xml : " + pathToFile);
			
			//read all of the text from the json file into a string
			ArrayList<String> listOfLines = (ArrayList<String>) Files.readAllLines(pathToFile);
			String entireFileText = "";
			for(String each: listOfLines){
				entireFileText += each;
			}
			
			//use the text we read in to create an XML string
			//System.out.println("about to create an xml string with the string: " + entireFileText);
			String XMLText = XML.toString(new JSONObject(entireFileText), "root");
				
			//write that XML string back into another file
			//System.out.println("about to write the follwing xml string to a file: " + XMLText);
			//System.out.println("parent path (which is where we will create the xml from the json) is: " + pathToFile.getParent().toString());
			File XMLFile = new File(pathToFile.getParent().toString() + "\\xmlVersionWithRoot.xml");
			//System.out.println(XMLFile);
			java.io.FileWriter writer = new java.io.FileWriter(XMLFile); //this should just be whatever the title of the .json was
			writer.write(XMLText);
			writer.close();
	
		}
		
	}
}
