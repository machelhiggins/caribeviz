package com.uwiseismic.ergo.ui.wizards.ingest;

import java.io.File;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;

import com.uwiseismic.ergo.datasets.properties.XMLDatasetProperties;

import edu.illinois.ncsa.ergo.gis.datasets.properties.DatasetProperties;
import edu.illinois.ncsa.ergo.gis.ui.wizards.ingest.Messages;
import edu.illinois.ncsa.ergo.gis.ui.wizards.ingest.SelectDatasetPage;
import ncsa.eclipse.core.utils.EclipseUtils;

public class XMLDatasetIngestPage  extends SelectDatasetPage
{
	public final static String INPUT_XML_FILE = "INPUT_XML_FILE"; //$NON-NLS-1$

	public XMLDatasetIngestPage()
	{
		super();
		init();
	}

	/**
	 *
	 */
	public void init()
	{
		setTitle(Messages.getString("XMLDatasetIngestPage.ingestTable")); //$NON-NLS-1$
		setDescription(Messages.getString("XMLDatasetIngestPage.enterPath")); //$NON-NLS-1$
	}

	/**
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent)
	{
		Composite container = EclipseUtils.createFormishContainer(parent);

		registerFileBrowsingText(container, INPUT_XML_FILE,
				Messages.getString("XML File"), SWT.NONE, true, new ModifyListener() { //$NON-NLS-1$
					public void modifyText(ModifyEvent e)
					{
						dialogModified();
					}
				});
//TODO: WHAT THE HELL DOES THIS METHOD DO?
		dialogModified();

		setControl(container);
	}

	/**
	 * 
	 */
	protected void dialogModified()
	{
		setPageComplete(false);

		if (getValue(INPUT_XML_FILE).equals("") || !new File(getValue(INPUT_XML_FILE)).exists()) { //$NON-NLS-1$
			setErrorMessage(Messages.getString("XMLDatasetIngestPage.mustChoosePath")); //$NON-NLS-1$
			return;
		}

		setErrorMessage(null);
		setPageComplete(true);
		return;
	}

	/**
	 * 
	 * @return
	 */
	public String[] getDatasetSchemaFields()
	{
		String fileStr = getValue(INPUT_XML_FILE);
		URI fileURI = new File(fileStr).toURI();		
		return new String[0];
		
	}

	/**
	 * 
	 * @return
	 */
	public String getFeatureTypeName()
	{
		return null;
	}

	/**
	 * 
	 * @return
	 */
	public String getGeometryType()
	{
		return null;
	}

	/**
	 * 
	 * @return
	 */
	public List<URI> getSourceFiles()
	{
		List<URI> files = new LinkedList<URI>();
		File f = new File(getValue(INPUT_XML_FILE));
		files.add(f.toURI());
		return files;
	}

	/**
	 * 
	 * @return
	 */
	public DatasetProperties initializeDatasetProperties()
	{
		XMLDatasetProperties properties = new XMLDatasetProperties();
		return properties;
	}

	
}
