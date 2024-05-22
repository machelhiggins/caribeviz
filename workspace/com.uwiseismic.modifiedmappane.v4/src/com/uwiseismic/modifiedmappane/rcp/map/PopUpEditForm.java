package com.uwiseismic.modifiedmappane.rcp.map;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.DefaultFeatureCollections;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.uwiseismic.gis.util.ObjectToReal;
import com.uwiseismic.modifiedmappane.rcp.xmlreader.ReadXML;


/**
 * Build dialog which allows user to edit certain attributes of selected features.
 * These attributes can be found in the EditableAttributes.xml file
 * 
 * @author Sterling Ramroach (UWI)
 */
public class PopUpEditForm extends TitleAreaDialog {


	//Read the xml file to build the view
	private ReadXML r = new ReadXML();
	private HashMap<String, String[]> labelValues = null;
	private HashMap<String, String> descriptions = null;
	private HashMap<String, String> types = null;
	private ArrayList<SimpleFeature> features = new ArrayList<SimpleFeature>();
	private HashMap<String,Text>  fields = new HashMap<String,Text>();
	private HashMap<String, Combo> combos = new HashMap<String, Combo>();
	private ArrayList<Widget> allFields = new ArrayList<Widget>(); 
	private boolean okPressed = false;
	private String URL;
	private Object firstVal[];
	private Object lastVal[];
	Set <String>attNames;
	private Shell shell;
	
	/**
	 * Pops up over the main application.
	 * @param parentShell = shell of the main application
	 */
	public PopUpEditForm(Shell parentShell) {		
		super(parentShell);
		this.shell = parentShell;
		r.read();
		labelValues = r.getAttr();
		descriptions = r.getDeacriptions();
		types = r.getTypes();
		okPressed = false;
		URL = r.getURL();
		
	}


	/**
	 * Create window of the dialog.
	 */
	@Override
	public void create() {
		super.create();
		setTitle("Edit Building Vulnerability Attribute");
		setMessage("Edit the selected features", IMessageProvider.INFORMATION);
	}

	public void setFeatures(ArrayList<SimpleFeature> feats) {
		features = feats;
		attNames = labelValues.keySet();
		firstVal = new Object[attNames.size()];
		lastVal = new Object[attNames.size()];		
		int h = 0;
		for(String attName : attNames){
			boolean setFirst = false;
			for(SimpleFeature f : features){				
				if(!setFirst){
					firstVal[h] = f.getAttribute(attName);
					setFirst = true;
				}
				lastVal[h] = f.getAttribute(attName);
			}
			h++;
		}
		h = 0;
	}


	/**
	 * If no features were selected, but the user still tries to edit attributes,
	 * then the dialog will read: "No features were selected!"
	 */
	public void NoSel() {
		super.create();
		MessageBox errMsg = new MessageBox(shell, IMessageProvider.ERROR);
		errMsg.setText("No features were selected!");
		errMsg.setMessage("No features were selected!");
		errMsg.open();
	}
	
	public void errSelectLayer(){
		super.create();
		
		MessageBox errMsg = new MessageBox(shell, IMessageProvider.ERROR);
		errMsg.setText("No layer selected for edit.");
		errMsg.setMessage("Select layer in the Scenario to edit.");
		errMsg.open();
	}

	/**
	 * Adds text from xml file onto labels which are displayed within the dialog.
	 * Creates text fields for the user to enter data, all of which relate to a label.
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.None);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(2, false);

		container.setLayout(layout);
		container.setFocus();
		//** find common attribute values to populate display

		String populateString;
		int h =0;
		for(String attName : attNames){
			if(lastVal[h] != null &&
					firstVal[h] != null &&
					lastVal[h].equals(firstVal[h]))
				populateString = lastVal[h].toString();
			else
				populateString = "";

			Label lbt = new Label(container, SWT.NONE);
			lbt.setText(createLabel(attName));

			GridData data = new GridData();
			data.grabExcessHorizontalSpace = true;
			data.horizontalAlignment = GridData.FILL;
			String values[] = labelValues.get(attName); 
			if(values != null && values.length > 0 ){
				Combo combo = new Combo(container, SWT.DROP_DOWN | SWT.BORDER);
				combo.setLayoutData(data);
				combo.setFocus();
				int m = -1;
				for(int z = 0; z < values.length; z++){
					if(values[z].equals(populateString))
						m = z;
					combo.add(values[z]);
				}
				if( m > 0)					
					combo.select(m);
				combo.setText(populateString);
				combos.put(attName, combo);				
			}else{
				Text newField = new Text(container, SWT.BORDER);
				fields.put(attName, newField);
				newField.setLayoutData(data);
				newField.setText(populateString);
				newField.setFocus();
			}
			h++;
		}
		return area;
	}
	
	private String createLabel(String attName){
		return descriptions.get(attName)+" ("+attName+")";
	}
	
	@Override
	protected boolean isResizable() {
		return true;
	}


	/**
	 * Save content of the Text fields before the dialog closes.
	 */
	private void saveInput() {

		SimpleFeatureType featureType = features.get(0).getFeatureType();
		ArrayList <SimpleFeature> toCommit = new ArrayList<SimpleFeature>();
		for(int j = 0; j<features.size();j++){
			SimpleFeature f = (SimpleFeature) features.get(j);

			String ans;
			for(String name: fields.keySet()){
				AttributeType attType = featureType.getType(name);								
				ans = fields.get(name).getText();			
				if(!ans.equals("")){
					if(name.equals("struct_typ")){
						if(ans.equals("RM1") || ans.equals("RM2")){
							f.setAttribute(name, createObject(types.get(name), "RM"));							
							determineFullHASUZStuctureType(f, ans);							
						}
						else
							f.setAttribute(name, createObject(types.get(name), ans));
						
					}else{
//						System.out.println("Before "+name+" is "+f.getAttribute(name)
//							+"of type "+(f.getAttribute(name) != null ? f.getAttribute(name).getClass().getName() : "null"));
						f.setAttribute(name, createObject(types.get(name), ans));
//						System.out.println("After "+name+" is set to "+f.getAttribute(name)
//							+"of type "+(f.getAttribute(name) != null ? f.getAttribute(name).getClass().getName() : "null"));
					}
				}			
			}    
			for(String name: combos.keySet()){
				AttributeType attType = featureType.getType(name);
				ans = combos.get(name).getText();
				if(!ans.equals("")){
					if(name.equals("struct_typ")){
						if(ans.equals("RM1") || ans.equals("RM2")){
							f.setAttribute(name, createObject(types.get(name), "RM"));							
							determineFullHASUZStuctureType(f, ans);
						}
						else{
//							System.out.println("Before structure type to "+f.getAttribute("struct_typ"));
							f.setAttribute(name, createObject(types.get(name), ans));
//							System.out.println("After structure type to "+f.getAttribute("struct_typ"));
						}						
					}
					else{
//						System.out.println("Before "+name+" is "+f.getAttribute(name));
						f.setAttribute(name, createObject(types.get(name), ans));
//						System.out.println("After "+name+" is set to "+f.getAttribute(name));
					}
				}
			}
			toCommit.add(f);
		}
		

//		Job job = new Job("Committing edits") { //$NON-NLS-1$
//            protected IStatus run(IProgressMonitor monitor){
//                try {
//                	saveFeatureToOutputStream(toCommit, featureType);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }   
//                return Status.OK_STATUS;
//            }};
//        job.setUser(true);
//        job.schedule();   
		//** dont have the time nor energy to figure out Job class when I don't need to update the UI and it seems as if
		//** the documentation is garbage\has been updated for new Eclipse. So....send to thread and forget!		
		new CommitLazyThread(toCommit, featureType).start();
	}

	/**
	 * Delete the temporary file after it has been posted via HTTP request.
	 * @param file
	 */
	void deleteDir(File file) {
		File[] contents = file.listFiles();
		if (contents != null) {
			for (File f : contents) {
				deleteDir(f);
			}
		}
		file.delete();
	}

	
	public static File getSystemTempDir() throws IOException{
	    File temp = File.createTempFile("temp-file-name", ".tmp");
	 
	    String absolutePath = temp.getAbsolutePath();
	    String tempFilePath = absolutePath.
	        substring(0,absolutePath.lastIndexOf(File.separator));
	   
	    return new File(tempFilePath);
	}


	/**
	 * Saves the new edited feature locally before attempting to POST all files in the directory via HTTP request.
	 * If the post is successful, the file will be deleted.
	 * Else, the file remains in the directory until another attempt at posting is made. 
	 * @param f
	 */
	@SuppressWarnings("resource")
	public void saveFeatureToOutputStream(ArrayList<SimpleFeature> features, SimpleFeatureType featureType){
		//Get unique name for temporary files
		String storagePath = null;
		String temp = null;
				
		try{
			
			storagePath = getSystemTempDir().getAbsolutePath();
			String fn = ""+System.currentTimeMillis();
			temp = storagePath + "/fc_"+fn;
			
			int n=0;
			File file = new File(temp+""+n);
			while(file.exists()){
				n++;
				file = new File(temp+""+n);
			}
			file.mkdirs();
			temp = temp+n;

			//Save to shapefile
			File shapefile = new File(temp+"/"+fn+".shp");
			CoordinateReferenceSystem crs = null;
			DefaultFeatureCollection fc = new DefaultFeatureCollection();
			for(SimpleFeature f: features){
				fc.add(f);
				if(crs == null){
					crs = f.getFeatureType().getCoordinateReferenceSystem();
					if(crs == null)
						crs = DefaultGeographicCRS.WGS84;
				}
			}
			
			DataStoreFactorySpi dataStoreFactory = new ShapefileDataStoreFactory();
			Map<String, Serializable> params = new HashMap<String, Serializable>();

			params.put("url", shapefile.toURI().toURL());
			params.put("create spatial index", Boolean.TRUE);            
			ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory
					.createNewDataStore(params);            
			newDataStore.createSchema(featureType);			
			newDataStore.forceSchemaCRS(crs);
			DefaultFeatureCollections fcs;
			
			/*
			 * Write the features to the shapefile
			 */
			Transaction transaction = new DefaultTransaction("create");            
			String typeName = newDataStore.getTypeNames()[0];
			FeatureSource featureSource = newDataStore.getFeatureSource(typeName);

			if (featureSource instanceof FeatureStore) {
				FeatureStore featureStore = (FeatureStore) featureSource;

				featureStore.setTransaction(transaction);
				try {
					featureStore.addFeatures(fc);

					transaction.commit();

				} catch (Exception problem) {
					problem.printStackTrace();
					transaction.rollback();
					throw problem;

				} finally {
					transaction.close();
				}
			}
			newDataStore.dispose();
			//Save the output to .zip file

			File zipfile = new File(storagePath +"/"+ System.currentTimeMillis()+".zip");
			ZipOutputStream zos = null;
			File listing []  = getShapeFiles(shapefile);		

			zos = new ZipOutputStream(new FileOutputStream(zipfile));
			for(int i = 0; i < listing.length; i++){
//				System.out.println("Zipping for feature commit: "+listing[i]);				
				FileInputStream fis = new FileInputStream(listing[i]);
				ZipEntry zipEntry = new ZipEntry(listing[i].getName());
				zos.putNextEntry(zipEntry);

				byte[] bytes = new byte[1024];
				int length;
				while ((length = fis.read(bytes)) >= 0) {
					zos.write(bytes, 0, length);
				}
				fis.close();				
				zos.closeEntry();

			}

			if(!shapefile.exists())
				System.err.println(this.getClass().getName()+": Error creating temporary shapefile for commit.");
			else{
				URL url;
				HttpURLConnection connection = null;  
				//Create connection
				url = new URL(URL);
				String charset = "UTF-8";
				String param = "anybody";

				String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.
				String CRLF = "\r\n"; // Line separator required by multipart/form-data.

				connection = (HttpURLConnection)url.openConnection();
				connection.setDoOutput(true);
				connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);


				OutputStream output = connection.getOutputStream();
				PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
				// Send normal param.
				writer.append("--" + boundary).append(CRLF);
				writer.append("Content-Disposition: form-data; name=\"userid\"").append(CRLF);
				writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
				writer.append(CRLF).append(param).append(CRLF).flush();


				// Send binary file.
				writer.append("--" + boundary).append(CRLF);
				writer.append("Content-Disposition: form-data; name=\"features\"; filename=\"" + shapefile.getName() + "\"").append(CRLF);				   
				//					    writer.append("Content-Type: application/octet-stream").append(CRLF);
				writer.append("Content-Type: application/zip").append(CRLF);

				writer.append("Content-Transfer-Encoding: binary").append(CRLF);
				writer.append(CRLF).flush();
				//					    Files.copy(shapefile.toPath(), output);

				FileInputStream fin = new FileInputStream(zipfile);
				byte buffer[]  = new byte[1024];
				int read = 0;
				while((read = fin.read(buffer, 0, buffer.length)) > 0){
					output.write(buffer, 0, read);
				}
				fin.close();


				output.flush(); // Important before continuing with writer!
				writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.

				// End of multipart/form-data.
				writer.append("--" + boundary + "--").append(CRLF).flush();

				// Request is lazily fired whenever you need to obtain information about response.
				int responseCode = ((HttpURLConnection) connection).getResponseCode();
				System.out.println("Commit HTTP Response Code : " + responseCode); // Should be 200		

				deleteDir(new File(temp));
			}
		}
		catch (Exception e) {
			deleteDir(new File(temp));
			e.printStackTrace();
		}
	}

	/**
	 * Used to POST all the changed made to a target URL.
	 * @param targetURL
	 * @param urlParameters
	 * @return
	 */
	public boolean excutePost(String targetURL, String urlParameters)
	{
		URL url;
		HttpURLConnection connection = null;  
		try {
			//Create connection
			url = new URL(targetURL);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", 
					"application/x-www-form-urlencoded");

			connection.setRequestProperty("Content-Length", "" + 
					Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");  

			connection.setUseCaches (false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			//Send request
			DataOutputStream wr = new DataOutputStream (
					connection.getOutputStream ());
			wr.writeBytes (urlParameters);
			wr.flush ();
			wr.close ();
			connection.disconnect();
			return true;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			connection.disconnect();
		}
		return false;
	}

	/**
	 * When the user is finished editing features, the ok button click in the dialog executes this function.
	 */
	@Override
	protected void okPressed() {
		saveInput();
		okPressed = true;
		super.okPressed();
	}

	public boolean didUserSubmit(){
		return okPressed;
	}
	
	public  File[] getShapeFiles(File shapefile){
		String shapefilename = shapefile.getName();
		shapefilename = shapefilename.substring(0,shapefilename.lastIndexOf('.'));
		return  shapefile.getParentFile().listFiles(
				(FileFilter)new PopUpEditForm.ShapeFileFilter(shapefilename));
	}
	
	private String determineFullHASUZStuctureType(SimpleFeature feature, String structType){
		int numOfStories = ObjectToReal.getMeInteger(feature.getAttribute("no_stories")).intValue();
		String likelyStructureStrHAZUS = structType;

		if(structType.equals("RM1")){
			if(numOfStories <= 3)
				likelyStructureStrHAZUS =  likelyStructureStrHAZUS+"L";
			else
				likelyStructureStrHAZUS =  likelyStructureStrHAZUS+"M";
		}
		else if(structType.equals("RM2")){
			likelyStructureStrHAZUS =  likelyStructureStrHAZUS+"L";	
		}
		else if(structType.equals("C1") 
				|| structType.equals("C2")){
			if(numOfStories <= 3)
				likelyStructureStrHAZUS =  likelyStructureStrHAZUS+"L";
			else if(numOfStories > 3 && numOfStories <= 7)
				likelyStructureStrHAZUS =  likelyStructureStrHAZUS+"M";
			else
				likelyStructureStrHAZUS =  likelyStructureStrHAZUS+"H";
		}			
		else if(structType.equals("W1")){
			likelyStructureStrHAZUS =  likelyStructureStrHAZUS+"L";
		}
		else if(structType.equals("S1")){
			likelyStructureStrHAZUS =  likelyStructureStrHAZUS+"L";
		}
		else if(structType.equals("PC1")){
			// nothing to do here
		}
		else if(structType.equals("PC2")){
			if(numOfStories <= 3)
				likelyStructureStrHAZUS =  likelyStructureStrHAZUS+"L";
			else if(numOfStories > 3 && numOfStories <= 7)
				likelyStructureStrHAZUS =  likelyStructureStrHAZUS+"M";
			else
				likelyStructureStrHAZUS =  likelyStructureStrHAZUS+"H";
		}
		else if(structType.equals("URM")){
			likelyStructureStrHAZUS =  likelyStructureStrHAZUS+"L";
		}
		
		feature.setAttribute("str_typ2", likelyStructureStrHAZUS);
		return likelyStructureStrHAZUS;
	}
	
	private Object createObject(String attType, String val){
		try{
			if(attType.equals("Long")){			
				return Long.getLong(val);
			}
			else if(attType.equals("Integer")){
				return new Integer(val);
			}
			else if(attType.equals("Double")){
				return Double.valueOf(val);
			}
			else{ //** then it must be a string.. 
				return val;
			}
		}catch (Exception ex){
			//setMessage("Edit the selected features", IMessageProvider.INFORMATION);
			setMessage("Could not format one of the numbers!", IMessageProvider.ERROR);
			throw ex;
		}
	}

	class ShapeFileFilter implements FileFilter{

		String shapefilename;

		public ShapeFileFilter(String shapefilename){
			this.shapefilename = shapefilename;
		}

		@Override
		public boolean accept(File f) {
			if(f.getName().matches(shapefilename+".*"))
				return true;
			return false;
		}				
	}
	
	/**
	 * Refused to learn how Job works in the background since no instructions available and I DONT UPDATE UI
	 * 
	 * @author machel
	 *
	 */
	class CommitLazyThread extends Thread{
		private ArrayList<SimpleFeature> features;
		private SimpleFeatureType featureType;
	
		public CommitLazyThread(ArrayList<SimpleFeature> features, SimpleFeatureType featureType){
			this.features = features;
			this.featureType = featureType;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run(){
			saveFeatureToOutputStream(features, featureType);
		}
	}

} 