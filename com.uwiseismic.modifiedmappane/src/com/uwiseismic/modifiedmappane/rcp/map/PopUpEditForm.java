package com.uwiseismic.modifiedmappane.rcp.map;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.DefaultFeatureCollections;
import org.opengis.feature.simple.SimpleFeature;

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
	public ArrayList<String> lableNames = new ArrayList<String>();
	public ArrayList<SimpleFeature> features = new ArrayList<SimpleFeature>();
	public Text [] fields;
	public int counter=0;

	/**
	 * Pops up over the main application.
	 * @param parentShell = shell of the main application
	 */
	public PopUpEditForm(Shell parentShell) {
		super(parentShell);
		r.read();
		lableNames = r.getAttr();
		fields = new Text[lableNames.size()];
	}


	/**
	 * Create window of the dialog.
	 */
	@Override
	public void create() {
		super.create();
		setTitle("Edit Feature");
		setMessage("Edit the selected features", IMessageProvider.INFORMATION);
	}


	public void create(ArrayList<SimpleFeature> f) {
		super.create();
		features = f;
		setTitle("Edit Feature");
		setMessage("Edit the selected features", IMessageProvider.INFORMATION);
	}


	/**
	 * If no features were selected, but the user still tries to edit attributes,
	 * then the dialog will read: "No features were selected!"
	 */
	public void NoSel() {
		super.create();
		setTitle("Edit Feature");
		setMessage("No features were selected!", IMessageProvider.ERROR);
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
		for(int i=0;i<lableNames.size();i++){
			createEditFields(container,lableNames.get(i));
		}
		return area;
	}

	private void createEditFields(Composite container,String l) {
		Label lbt = new Label(container, SWT.NONE);
		lbt.setText(l);

		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;

		fields[counter] = new Text(container, SWT.BORDER);
		fields[counter].setLayoutData(data);
		fields[0].setFocus();
		counter++;
	}


	@Override
	protected boolean isResizable() {
		return true;
	}


	/**
	 * Save content of the Text fields before the dialog closes.
	 */
	private void saveInput() {
		String updatedFeature = "";

		for(int j = 0; j<features.size();j++){
			updatedFeature = features.get(j).toString();
			SimpleFeature f = (SimpleFeature) features.get(j);

			for(int i=0; i<fields.length;i++){
				f.setAttribute(lableNames.get(i).toString(), fields[i].getText());
				updatedFeature += ", " + lableNames.get(i).toString() + " = " + fields[i].getText();  
			}

			//Commit via HTTP request
			saveFeatureToOutputStream(f);

			updatedFeature = "";
		}

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



	/**
	 * Saves the new edited feature locally before attempting to POST all files in the directory via HTTP request.
	 * If the post is successful, the file will be deleted.
	 * Else, the file remains in the directory until another attempt at posting is made. 
	 * @param f
	 */
	public void saveFeatureToOutputStream(SimpleFeature f){

		try{
			//Get unique name for temporary files
			String fn = ""+System.currentTimeMillis();
			String temp = System.getProperty("user.home") + "/NCSA/FeatureEdits/fc_"+fn;

			int n=0;
			File file = new File(temp+""+n);
			while(file.exists()){
				n++;
				file = new File(temp+""+n);
			}
			file.mkdirs();
			temp = temp+n;

			//Save to shapefile
			File newFile = new File(temp+"/"+fn+".shp");

			DataStoreFactorySpi dataStoreFactory = new ShapefileDataStoreFactory();
			Map<String, Serializable> params = new HashMap<String, Serializable>();

			params.put("url", newFile.toURI().toURL());
			params.put("create spatial index", Boolean.TRUE);            
			ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory
					.createNewDataStore(params);            
			newDataStore.createSchema(f.getFeatureType());
			newDataStore.forceSchemaCRS(f.getFeatureType().getCoordinateReferenceSystem());
			DefaultFeatureCollections fcs;
			DefaultFeatureCollection fc = new DefaultFeatureCollection();
			fc.add(f);
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

			byte[] bytes;

			//Save the output to .zip file
			File zipfile = new File(temp+"/"+fn+".zip");
			ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipfile));
			File listing []  = new File(temp).listFiles();		
			for(int i = 0; i < listing.length; i++){
				if(listing[i].getName().matches(fn+"\\..*")){
					FileInputStream fis = new FileInputStream(listing[i]);
					ZipEntry zipEntry = new ZipEntry(listing[i].getName());
					zos.putNextEntry(zipEntry);

					bytes = new byte[1024];
					int length;
					while ((length = fis.read(bytes)) >= 0) {
						zos.write(bytes, 0, length);
					}
					fis.close();				
					zos.closeEntry();
				}
			}		
			zos.close();

			//////////////////////////////////////////////////////////////////////////
			/* Method 1:
			 * param = the zip file 'zipfile'
			 * target = the URL that receives the zip file
			 *
			 * IF executePost is successful (i.e., returns true) then delete the zipped file.
			 */

			//if(executePost(target,param))deleteZip();
			///////////////////////////////////////////////////////////////////////////

			//////////////////////////////////////////////////////////////////////////////////////////////
			//Method 2:
			//		 Alternate method: add org.apache.commons.httpclient to required bundles and post the file.
			//		HttpURLConnection request = null; 
			//		HttpClient client = new HttpClient(); 
			//		client.getState().setCredentials(
			//		    new AuthScope(hostip, port), 
			//		    new UsernamePasswordCredentials("username", "password")); 
			//		PutMethod post = new PutMethod(url); 
			//		post.setRequestHeader("Content-Encoding", "gzip");
			//	       post.setDoAuthentication(true);
			//
			//	     
			//	            InputStream bais = new ByteArrayInputStream(bytes);
			//	        post.setRequestBody(bais);
			//	        try {
			//	            int status = client.executeMethod(post);
			//
			//	        } finally {
			//	            // release any connection resources used by the method
			//	            post.releaseConnection();
			//	        }
			////////////////////////////////////////////////////////////////////////////////////////////////

			//			if post is successful, then delete the zipped file:
			//			deleteDir(file);


		} catch (Exception e) {
			// TODO Auto-generated catch block
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
		super.okPressed();
	}

} 