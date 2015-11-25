import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

import org.json.*;



/**
 *Basically a script that sets the FileVisitor off on its journey
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
			System.out.println("IO EXCEPTION INCURRED DURING THE VISITOR'S WALK");
			e.printStackTrace();
		}finally{
			System.out.println("finished walking file tree after approx " + ((System.currentTimeMillis() - startTime)/1000)  + " seconds : )" );
		}
		
	}

	
	
	
	/**
	 * explores the specified directory and all descendant directories. Does a depth first search but that order that children are chosen to be traversed is not defined. SHouldn't matter here.
	 * @author max
	 *
	 */
	private static class XmlConverterVisitor implements FileVisitor{

		
		
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
				//so we have encountered a file that is purportedly .json, we should attempt to produce an xml file from each of the json objects that it contains
		//	try{ //THIS IS COMMENTED OUT TO FIX BUGS WITH MALFORMED FILES
				//convertToXml(pathToFile);
				//split the json file with several json article objects inside into several json objects. One for each "article"
				ArrayList<JSONObject> JSONObjects = separateJSONObjectsInFile(pathToFile);
				//for each of these JSONObjects, create an individual JSON file for it, and then create an XML file of that.
				for(JSONObject each: JSONObjects){
					//create the JSON file
					createJSONFileFromJSONObject(each, pathToFile);
					//create the XML file
					createXMLFileFromJSONObject(each, pathToFile);
				}
		//	}catch(IOException e){ 
		//		System.out.println("attempted to convert the following file to XML and incurred an io exception: " + pathToFile);
		//		e.printStackTrace();
		//	}
			}else{
			//	System.out.println("non json file encountered: " + arg0);
			}
			return java.nio.file.FileVisitResult.CONTINUE;
		}


		/**
		 * takes a json file that has several json objects defined within and returns a list of those json objects
		 * @param pathToFile the path to the file that is to be separated into its constituent json objects
		 * @return ArrayList<JSONObject> a list of all of the JSONObjects that are defined in the supplied file
		 * @throws IOException 
		 */
		private ArrayList<JSONObject> separateJSONObjectsInFile(Path pathToFile) throws IOException {
			
			//System.out.println("about to separate the file: " + pathToFile.toString());

			ArrayList<String> listOfJSONStrings = (ArrayList<String>) Files.readAllLines(pathToFile, Charset.availableCharsets().get("ISO-8859-1"));
			ArrayList<JSONObject> listOfJSONObjects = new ArrayList<>();
			for(String each: listOfJSONStrings){
				listOfJSONObjects.add(new JSONObject(each));
			}
		//	System.out.println("separated successfully");
			return listOfJSONObjects;
		}

		/**
		 * creates a JSONFile at the specified location
		 * @param JSONObject the JSONObject to create a file of
		 * @param pathToOriginalFile the location that we are creating the file in
		 * @return File the .json file created
		 * @throws IOException 
		 */
		private void createJSONFileFromJSONObject(JSONObject JSONObj, Path pathToOriginalFile) throws IOException {
			File JSONFile = new File(pathToOriginalFile.getParent().toString() + "\\splitJson" + JSONObj.hashCode() + ".json"); //TODO: should query the actual JSON object with the "article name" key or something to get the actual name of the article to name the file
			java.io.FileWriter writer = new java.io.FileWriter(JSONFile);
			writer.write(JSONObj.toString());
			writer.close();
		}
		
		/**
		 * creates an XMLFile at the specified location
		 * @param JSONObj the JSONObject that we are "converting" into an xml file
		 * @param pathToOriginalFile the location that we are creating the file in
		 * @throws IOException 
		 */
		private void createXMLFileFromJSONObject(JSONObject JSONObj,
				Path pathToOriginalFile) throws IOException {
			File XMLFile = new File(pathToOriginalFile.getParent().toString()  + "\\splitXML" + JSONObj.hashCode() + ".xml"); //TODO: should query the actual JSON object with the "article name" key or something to get the actual name of the article to name the file
			java.io.FileWriter writer = new java.io.FileWriter(XMLFile);
			writer.write(XML.toString(JSONObj, "root")); //note: using "root" as the enclosing json tags. This seems conventional.
			writer.close();
		}
		
		
		
/*		//TODO: at the moment this method only hackily checks that the xml string that we will be saving to file is "well formed" by relying upon the call to XML.toString to throw the JSONException (presumably... havent even looked at code). 
		*//**
		 * attempts to create an xml version of a file that is purported to be in the .json format.
		 * This method will be attempted with every file that is in the directory tree of whatever our start point directory is.
		 * For now, it does not handle poorly formed json gracefully, will throw a runtime exception.
		 * @param pathToFile the path of the json file that is to be converted
		 *//*
		private void convertToXML(Path pathToFile) throws IOException {
			//System.out.println("about to convert this file to xml : " + pathToFile);
			
			//read all of the text from the json file into a string
			ArrayList<String> listOfLines = (ArrayList<String>) Files.readAllLines(pathToFile);
			String entireFileText = "";
			for(String each: listOfLines){
				entireFileText += each;
			}
			//> at this point we have a full string with all 10 articles or watev at entireFileText
			//use the text we read in to create an XML string
			JSONObject jsonFileText = new JSONObject(entireFileText);
			String XMLText = XML.toString(new JSONObject(jsonFileText), "root");
			//> at this point the string at XMLTest only has the first json object as an xml compliant string
			//write that XML string back into another file
			//System.out.println("about to write the follwing xml string to a file: " + XMLText);
			//System.out.println("parent path (which is where we will create the xml from the json) is: " + pathToFile.getParent().toString());
			File XMLFile = new File(pathToFile.getParent().toString() + "\\xmlVersionWithRoot" + Math.random() + ".xml"); //TODO: the mathrandom here is just to differentiate the diff files that will be put into the same directory. ultimately 
			//System.out.println(XMLFile);
			java.io.FileWriter writer = new java.io.FileWriter(XMLFile); //this should just be whatever the title of the .json was
			writer.write(XMLText);
			writer.close();
	
		}*/
		

		//BARELY TOUCHED INTERFACE REQUIRED METHODS/////////
		
		@Override
		public FileVisitResult postVisitDirectory(Object arg0, IOException arg1)
				throws IOException {
			return java.nio.file.FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult preVisitDirectory(Object arg0,
				BasicFileAttributes arg1) throws IOException {
			return java.nio.file.FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFileFailed(Object arg0, IOException arg1)
				throws IOException {
			throw new IOException();
		}
		
		////////////////////////////////////////
	}
}
