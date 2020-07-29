package Test1.OSHDB_Test2;

import java.sql.Connection;
import java.sql.SQLException;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.heigit.bigspatialdata.oshdb.api.db.OSHDBDatabase;
import org.heigit.bigspatialdata.oshdb.api.db.OSHDBH2;
import org.heigit.bigspatialdata.oshdb.api.db.OSHDBJdbc;
import org.heigit.bigspatialdata.oshdb.api.generic.function.SerializableBinaryOperator;
import org.heigit.bigspatialdata.oshdb.api.generic.function.SerializableFunction;
import org.heigit.bigspatialdata.oshdb.api.generic.function.SerializableSupplier;
import org.heigit.bigspatialdata.oshdb.api.mapreducer.OSMContributionView;
import org.heigit.bigspatialdata.oshdb.api.mapreducer.OSMEntitySnapshotView;
import org.heigit.bigspatialdata.oshdb.api.object.OSMContribution;
import org.heigit.bigspatialdata.oshdb.osh.OSHEntity;
import org.heigit.bigspatialdata.oshdb.osm.OSMEntity;
import org.heigit.bigspatialdata.oshdb.osm.OSMType;
import org.heigit.bigspatialdata.oshdb.util.OSHDBBoundingBox;
import org.heigit.bigspatialdata.oshdb.util.OSHDBTag;
import org.heigit.bigspatialdata.oshdb.util.OSHDBTagKey;
import org.heigit.bigspatialdata.oshdb.util.OSHDBTimestamp;
import org.heigit.bigspatialdata.oshdb.util.celliterator.ContributionType;
import org.heigit.bigspatialdata.oshdb.util.exceptions.OSHDBKeytablesNotFoundException;
import org.heigit.bigspatialdata.oshdb.util.geometry.Geo;
import org.heigit.bigspatialdata.oshdb.util.time.OSHDBTimestamps;
import org.heigit.bigspatialdata.oshdb.util.time.OSHDBTimestamps.Interval;
import org.heigit.bigspatialdata.oshdb.util.OSHDBBoundingBox;
import org.heigit.bigspatialdata.oshdb.util.tagtranslator.OSMTag;
import org.heigit.bigspatialdata.oshdb.util.tagtranslator.OSMTagKey;
import org.heigit.bigspatialdata.oshdb.util.tagtranslator.TagTranslator; 
import org.heigit.bigspatialdata.oshdb.util.tagtranslator.*; 

public class StreamTest {
	
	public static void main(String[] args) 
			throws SQLException, 
			ClassNotFoundException, 
			Exception {
		
// -----------------------------------------------------------------------
// 0. VARIABLES
// -----------------------------------------------------------------------
		String timeStart = "2009-12-31";
		//String timeStart = "2011-09-12";
		String timeEnd = "2010-12-31";
		
		OSHDBBoundingBox bbox = new OSHDBBoundingBox(8.573179,49.352003,8.79405,49.459693); //heid
		//OSHDBBoundingBox bbox = new OSHDBBoundingBox(18.496906,4.317599,18.594561,4.487146); // car
		//OSHDBBoundingBox bbox = new OSHDBBoundingBox(34.895,-17.1296,35.3147,-16.3102); // mal
		//OSHDBBoundingBox bbox = new OSHDBBoundingBox(85.268106,27.667949,85.375557,27.75134); // nep wsen
		//OSHDBBoundingBox bbox = new OSHDBBoundingBox(124.896758,11.180703,125.078644,11.342878); // tac
		//OSHDBBoundingBox bbox = new OSHDBBoundingBox(-72.569771,18.339898,-72.158934,18.630734); // pap
		
		String output = "C:\\Users\\hanna\\Desktop\\Dissertation\\3_Analyzing\\Data\\hed-nodes.csv";
		
// -----------------------------------------------------------------------
// 1. PATH TO OSHDB
// -----------------------------------------------------------------------
		String path = "C:\\Users\\hanna\\Desktop\\Dissertation\\3_Analyzing\\Data\\Extracts\\heidelberg.oshdb"; 
		OSHDBH2 oshdb = new OSHDBH2(path);

// -----------------------------------------------------------------------
// 2. Filter OSHDB data 
// -----------------------------------------------------------------------
			// Filter the data to get a stream of OSH Entities
			Stream<OSHEntity> entityStream = OSMContributionView
			        .on(oshdb)
			        .osmType(OSMType.NODE)
			        //.osmType(OSMType.WAY)
			        .timestamps(timeStart, timeEnd) // Start and end timestamps
			        .areaOfInterest(bbox) // Area of interest
			        .filter(contrib -> contrib.is(ContributionType.CREATION)) // Is a created entity
			        .map(contrib -> contrib.getOSHEntity()) // Get the OSH entities
			        .stream(); // Get stream of the data
			
// -----------------------------------------------------------------------
// 3. Set up CSV file writer 
// -----------------------------------------------------------------------

			try {
				
				FileWriter csvWriter = new FileWriter(output);

				csvWriter.append(
						"OSH_ID, "
						+ "OSH_TYP, "
						+ "OSH_BB_a, "
						+ "OSH_BB_b, "
						+ "OSH_BB_c, "
						+ "OSH_BB_d, "
						+ "OSM_UID, "
						+ "OSM_CHG, "
						+ "OSM_VER, "						
						+ "OSM_TME, "
						+ "OSM_KYS,"
						+ "OSM_VIS,"
						+ "OSM_TGS\n");
				
// -----------------------------------------------------------------------
// 4. Loop through each of the OSH entities 
// -----------------------------------------------------------------------
				// Loop through each OSHEntity in the stream
				entityStream.forEach(s -> {		
					
					// Get the data of interest from each OSHEntity from the stream
					OSHDBBoundingBox box = s.getBoundingBox(); // Bounding box
					//int [] keys = s.getRawTagKeys(); // Tag keys
					Iterable<? extends OSMEntity> versions = s.getVersions(); // OSMEntity versions
					long oshID = s.getId(); // Get the OSH ID
					OSMType type = s.getType(); 
					// System.out.println(StreamSupport.stream(versions.spliterator(), false).count()); // Get the total number of versions of each entity
			
// -----------------------------------------------------------------------
// 5. Loop through each OSM entity in the parent OSH entity	
// -----------------------------------------------------------------------
					// Loop through each of the OSM entities in the OSH entity
					for(OSMEntity b: versions){
						
						// Get the data of interest for each OSMEntity
						Iterable<OSHDBTag> tgs = b.getTags(); // Get the tags
						OSHDBTimestamp tme = b.getTimestamp(); // Get the timestamp 
						int ver = b.getVersion(); // Get the version number 
						int id = b.getUserId(); // Get the user id
						long change = b.getChangesetId(); // Get the changeset id
						Boolean vis = b.isVisible(); 

// -----------------------------------------------------------------------
// 6. Get the tags for each OSM entity
// -----------------------------------------------------------------------
						ArrayList <String> taglist = new ArrayList<String>();
						ArrayList <String> keys = new ArrayList<String>();
						
						// Initialize the TagTranslator
						TagTranslator r;
						try {
							
							// Connect to the database 
							r = new TagTranslator(((OSHDBJdbc) oshdb).getConnection());
							// Loop through to get each tag 
							for(OSHDBTag t: tgs) {
								// Get the text representation of the tag 
								OSMTag osmTag = r.getOSMTagOf(t);
								
								String key = osmTag.getKey(); // string representation of the tag key
						        String val = osmTag.getValue(); //   ... of the tag value;
						        String keyval = key + '=' + val; 
						        keyval = keyval.replace(',', ';'); // Replace commas in the tags so that it won't mess up the csv file
						        taglist.add(keyval);    
						        keys.add(key);
						        					        
							}					
							
						} catch (OSHDBKeytablesNotFoundException e1) {
							e1.printStackTrace();
						}
// -----------------------------------------------------------------------
// 7. Write data to a CSV file 					
// -----------------------------------------------------------------------
						
						try {

							csvWriter.append(
									String.valueOf(oshID) + ',' + 
									type.toString() + ',' +
									box.toString() + ',' +
									String.valueOf(id) + ',' +
									String.valueOf(change) + ',' +
									String.valueOf(ver) + ',' + 
									tme.toString() + ',' +
									String.join("@", keys) + ',' +
									vis.toString() + ',' + 
									String.join("@", taglist) + '\n'
									);
								
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
						
					} // End of loop through OSH entity
					
				}); // End of loop through stream
				
				// Close the stream
				csvWriter.flush();
				csvWriter.close();
				
			} //End of the try block
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // End of the catch block
		} // End of main method

}
