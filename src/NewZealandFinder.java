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

import org.json.JSONObject;


public class NewZealandFinder {

	
	private static ArrayList<Path> NZFiles = new ArrayList<>();
	
	public static void main(String[] args){
		doFileWalk(new NewZealandFinderVisitor());
	}


	private static void doFileWalk(FileVisitor fileVisitor) {
		//start the file visitor on its journey
		//hardcode the start point path !!!
		Path initPath = FileSystems.getDefault().getPath("C:\\!2015SCHOLARSHIPSTUFF\\CompleteSampleCopy\\TAS");
		long startTime = System.currentTimeMillis();
		try {
			System.out.println("starting walk of file tree...");
			Files.walkFileTree(initPath, fileVisitor);
		} catch (IOException e) {
			System.out.println("IO EXCEPTION INCURRED DURING THE VISITOR'S WALK");
			e.printStackTrace();
		}finally{
			System.out.println("finished walking file tree after approx " + ((System.currentTimeMillis() - startTime)/1000)  + " seconds : )" );
			for(Path each: NZFiles){
				System.out.println(each.toString());
			}
		}
		
	}


	public static class NewZealandFinderVisitor implements FileVisitor{

		

		@Override
		public FileVisitResult visitFile(Object file,
				BasicFileAttributes attrs) throws IOException {
			//just look for the string "new_zealand" as the heading. Note that this will only get like 30 % of the articles that are at all relevant to nz
			Path possibleJsonFilePath = (Path)file;
			File possibleJsonFile = possibleJsonFilePath.toFile();
			
			if(possibleJsonFile.toString().substring(possibleJsonFile.toString().length() - 5, possibleJsonFile.toString().length()).equals(".json")){
				//System.out.println("about to separate the file: " + pathToFile.toString());
				
				JSONObject JSONObj = new JSONObject(Files.readAllLines(possibleJsonFilePath, Charset.availableCharsets().get("ISO-8859-1")).get(0));
				if(JSONObj.getString("heading").toLowerCase().contains("new zealand")){
					NZFiles.add(possibleJsonFilePath);
					String newFileLocationName = ("C:\\!2015SCHOLARSHIPSTUFF\\dummyNzTest" + "\\" + Main.sanitiseFileName(JSONObj.getString("heading")) + ".json");
					File JSONFile = new File(newFileLocationName);
					java.io.FileWriter writer = new java.io.FileWriter(JSONFile);
					writer.write(JSONObj.toString());
					writer.close();
				}
			}  //TODO:  slightly pernicious bug: i will be overwriting nz files with the same file name. should just include the uniqueid as a suffix to the heading part of the file name
			
			return java.nio.file.FileVisitResult.CONTINUE;
		}

		//BARELY TOUCHED INTERFACE REQUIRED METHODS/////////
		
		@Override
		public FileVisitResult postVisitDirectory(Object arg0, IOException arg1)
				throws IOException {
			//extremely crude way of measuring the file visitors progress this is gross hack
			String str = arg0.toString();
			if(str.substring(str.length() - 4, str.length() - 2).equals("18") || str.substring(str.length() - 4, str.length() - 2).equals("17") || str.substring(str.length() - 4, str.length() - 2).equals("19")){ //TODO: remove this and replace with something better
				System.out.println("just finished searching through folder: " + str);
			}
			///////////////////////////////////////////////
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
		
	}

















}
