package com.uwiseismic.ergo.datasets.factories;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.geotools.data.DataStore;

import com.uwiseismic.ergo.datasets.XMLDataset;
import com.uwiseismic.ergo.datasets.properties.XMLDatasetProperties;

import edu.illinois.ncsa.ergo.gis.Dataset;
import edu.illinois.ncsa.ergo.gis.DatasetIngestor;
import edu.illinois.ncsa.ergo.gis.GISConstants;
import edu.illinois.ncsa.ergo.gis.SchemaProvider;
import edu.illinois.ncsa.ergo.gis.datasets.RasterDataset;
import edu.illinois.ncsa.ergo.gis.datasets.exporthandlers.FileCopyExportHandler;
import edu.illinois.ncsa.ergo.gis.datasets.factories.AbstractDatasetFactory;
import edu.illinois.ncsa.ergo.gis.datasets.properties.DatasetProperties;
import edu.illinois.ncsa.ergo.gis.datasets.properties.FileDatasetProperties;
import edu.illinois.ncsa.ergo.gis.datasets.properties.GISDatasetProperties;
import edu.illinois.ncsa.ergo.gis.datasets.properties.MappedDatasetProperties;
import edu.illinois.ncsa.ergo.gis.descriptors.SchemaDescriptor;
import edu.illinois.ncsa.ergo.gis.exceptions.DataIngestException;
import edu.illinois.ncsa.ergo.gis.exceptions.DatasetConversionException;
import edu.illinois.ncsa.ergo.gis.exceptions.DatasetCreationException;
import edu.illinois.ncsa.ergo.gis.exceptions.SchemaCreationException;
import edu.illinois.ncsa.ergo.gis.repositories.LocalRepository;
import edu.illinois.ncsa.ergo.gis.types.AttributeMap;
import edu.illinois.ncsa.ergo.gis.util.MiscUtils;
import ncsa.tools.common.util.PathUtils;

public class XMLDatasetFactory  extends AbstractDatasetFactory{
	private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());

	/**
	 * 
	 * @param monitor
	 * @param repository
	 * @param dataset
	 * @param baseDirectory
	 * @param fileName
	 * @param useFilters
	 * @throws DatasetConversionException
	 */
	public void exportDataset(SubProgressMonitor monitor, LocalRepository repository, Dataset dataset, String baseDirectory,
			String fileName, boolean useFilters) throws DatasetConversionException{
		FileCopyExportHandler handler = new FileCopyExportHandler();
		handler.setDataset(dataset);
		handler.setDestFile(new File(baseDirectory, ensurePrimaryFileExtension(fileName)));
		handler.setFactoryForDataset(this);
		handler.setRepository(repository);

		handler.handle(monitor);
	}

	/**
	 * 
	 * @param properties
	 * @param monitor
	 * @return
	 * @throws DatasetCreationException
	 */
	public Dataset createDataset(DatasetProperties properties, IProgressMonitor monitor) throws DatasetCreationException{
		URI remoteURI;
		try {
			FileDatasetProperties prop = (FileDatasetProperties) properties;
			remoteURI = repository.getRemoteDatasetURI(properties, prop.getName(), ".xml"); //$NON-NLS-1$
			logger.debug( remoteURI );

			File datasetReadableFile = repository.getDatasetReadableFile(remoteURI);

			MappedDatasetProperties mappedDatasetProperties = (MappedDatasetProperties) properties;

			XMLDataset dataset = readXMLDatasetFromFile(datasetReadableFile.toURI(), mappedDatasetProperties);
			dataset.setTypeId(getTypeId(mappedDatasetProperties));
			dataset.setFriendlyName(properties.getName());
			dataset.setDataId(properties.getDatasetUniqueId());			
						
//			String name = properties.getName();
//			name = cleanName(name);	

//			URI remoteDatasetURI = repository.getRemoteDatasetURI(prop, name, ".xml"); //$NON-NLS-1$
//			File localCacheFile = repository.getDatasetReadableFile(remoteDatasetURI);
//			URI localCacheURI = localCacheFile.toURI();
//
//			URL url;
//			try {
//				url = localCacheURI.toURL();
//			} catch (MalformedURLException e) {
//				logger.error("Could not read xml data", e); //$NON-NLS-1$
//				throw new DatasetCreationException("Could not read xml data", e); //$NON-NLS-1$
//			}
			
//			dataset.setXmlFileURL(url);
//			dataset.setFriendlyName(prop.getName());
//			dataset.setDataId(prop.getDatasetUniqueId());
//			dataset.setTypeId(XMLDataset.TYPE_ID);
//			dataset.setMetadata(prop.getMetadata());

			return dataset;
		} catch (URISyntaxException e) {
			throw new DatasetCreationException("Could not build URI for dataset", e); //$NON-NLS-1$
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			throw new DatasetCreationException("Could not build URI for dataset", e);
		} 

	}

	/**
	 * 
	 * @param properties
	 * @param dataset
	 * @param monitor
	 * @throws DataIngestException
	 */
	public void ingestDataset(DatasetProperties properties, Dataset dataset, IProgressMonitor monitor) throws DataIngestException{
		XMLDataset d = (XMLDataset) dataset;
		ingestXMLDataset(properties, d);
	}
	

	public void ingestDataset(DatasetProperties properties, DataStore store, List<URI> fileList)
			throws DataIngestException {
		if (!(repository instanceof DatasetIngestor)) {
			throw new DataIngestException("repository is not a DatasetIngestor"); //$NON-NLS-1$
		}

		if (!(properties instanceof XMLDatasetProperties)) {
			throw new DataIngestException("properties must be XMLDatasetProperties"); //$NON-NLS-1$
		}

		URI first = fileList.get(0);
		XMLDatasetProperties tdp = (XMLDatasetProperties) properties;
		String name = PathUtils.getName(first);
		tdp.setFilename(name);

		XMLDataset dataset = null;
		try {
			dataset = readXMLDatasetFromFile(first, tdp);
		} catch (DatasetCreationException e) {
			// TODO Auto-generated catch block
			logger.error(e);			
		}
		//dataset.setMapping(tdp.getMapping());
		//dataset.remap();
		dataset.setTypeId(properties.getTypeId());

		dataset.setMetadata(properties.getMetadata());
		//dataset.applyDefaultMetadata();

		ingestXMLDataset(properties, dataset);

		
	}

	/**
	 * 
	 * @return
	 */
	public DatasetProperties createNewDatasetProperties()
	{
		return new XMLDatasetProperties();
	}

	
	public void initializeDataset(Dataset dataset) { } 
			
	public void prepareInitializedProperties(DatasetProperties properties, DataStore store, IProgressMonitor monitor) {
	}


	public String getPrimaryFileExtension() {
		return "xml";
	}

	/**
	 * 
	 * @param file
	 * @param props
	 * @return
	 * @throws DatasetCreationException
	 */
	public XMLDataset readXMLDatasetFromFile(URI file, MappedDatasetProperties props) throws DatasetCreationException
	{
		if (MiscUtils.getFileExtension(file.getPath()).equalsIgnoreCase("xml")) { //$NON-NLS-1$
			//return createDatasetFromCSV(file, props);
			XMLDataset xmlDataset = new XMLDataset();
			
			File xmlFile;
			if (repository != null) {
				try {
					xmlFile = repository.getDatasetReadableFile(file);
				} catch (DatasetCreationException e1) {
					logger.error(e1);
					return null;
				}

			} else {
				xmlFile = new File(file);
			}
			if (!xmlFile.exists()) {
				logger.error("file doesn't exist:" + xmlFile.getAbsolutePath()); //$NON-NLS-1$
				return null;
			}						
			SAXReader reader = new SAXReader();       
	        Document document = null;
			try {
				document = reader.read(xmlFile);
			} catch (DocumentException e) {
				logger.error(e);
				return null;
			}							
			Element rootNode = document.getRootElement();			
			xmlDataset.setXMLDocument(rootNode);
			
			return xmlDataset;
		}

		return null;
	}
	
	public XMLDataset readXMLDatasetFromFile(URI file) throws DatasetCreationException{

		return readXMLDatasetFromFile(file, null);
	}
	
	
	/**
	 * 
	 * @param properties
	 * @param set
	 * @throws DataIngestException
	 */
	private void ingestXMLDataset(DatasetProperties properties, XMLDataset dataset) throws DataIngestException
	{
		if (!(repository instanceof DatasetIngestor)) {
			throw new DataIngestException("repository is not a DatasetIngestor"); //$NON-NLS-1$
		}

		if (!(properties instanceof XMLDatasetProperties)) {
			throw new DataIngestException("properties must be XMLDatasetProperties"); //$NON-NLS-1$
		}

		DatasetIngestor ingestRepo = (DatasetIngestor) repository;
		XMLDatasetProperties xdp = (XMLDatasetProperties) properties;
		
		try {
			String name = properties.getName();
			name = cleanName(name);
			URI convertedPath = repository.getRemoteDatasetURI(properties, name, ".xml"); //$NON-NLS-1$
	
			xdp.setFilename(name + ".xml"); //$NON-NLS-1$

			AttributeMap mapping = new AttributeMap();
			SchemaDescriptor schemaDescriptor = SchemaProvider.getSchemaDescriptor(dataset.getTypeId());
			mapping.setTargetSchema(schemaDescriptor);
			
			xdp.setMapping(mapping);

			File writableConverted = ingestRepo.getWritableFileForIngestion(convertedPath);
			writeXMLDatasetToFile(dataset, writableConverted);
			ingestRepo.ingestWritableFile(writableConverted, convertedPath);

//			URI remoteDatasetURI = repository.getRemoteDatasetURI(xdp, name, ".xml"); 
//			String locationDir = PathUtils.getParent(PathUtils.getParent(remoteDatasetURI)).toString();
//			if (dataset.getDataId().getLocation() == null) {
//				dataset.getDataId().setLocation(locationDir);
//			}
//			if (properties.getDatasetUniqueId().getLocation() == null) {
//				properties.getDatasetUniqueId().setLocation(locationDir);
//			}
		} catch (URISyntaxException e) {
			throw new DataIngestException("Could not figure out converted path", e); //$NON-NLS-1$
		} catch (SchemaCreationException e) {			
			throw new DataIngestException("Could not figure out converted path", e); //$NON-NLS-1$
		} catch (UnsupportedEncodingException e) {
			throw new DataIngestException("Could not figure out converted path", e); //$NON-NLS-1$
		} 
	}
	
	/**
	 * 
	 * @param dataset
	 * @param file
	 * @throws DataIngestException
	 */
	private void writeXMLDatasetToFile(XMLDataset dataset, File file) throws DataIngestException{
		URI parentURI;
		try {
			parentURI = PathUtils.getParent(file.toURI());
			File parent = new File(parentURI);
			parent.mkdirs();
			FileWriter out = new FileWriter(file);			
			dataset.getXMLDocument().write( out );
			out.close();

		} catch (URISyntaxException e) {
			throw new DataIngestException(e);
		} catch (IOException e) {
			throw new DataIngestException(e);
		}
	}

	private String cleanName(String name){
		name = name.replaceAll("/", "_"); //$NON-NLS-1$ //$NON-NLS-2$
		name = name.replaceAll(":", "_"); //$NON-NLS-1$ //$NON-NLS-2$
		name = name.replaceAll("\\*", "_"); //$NON-NLS-1$ //$NON-NLS-2$
		name = name.replaceAll("\"", "_"); //$NON-NLS-1$ //$NON-NLS-2$
		name = name.replaceAll("\\?", "_"); //$NON-NLS-1$ //$NON-NLS-2$
		name = name.replaceAll("\\/", "_"); //$NON-NLS-1$ //$NON-NLS-2$
		name = name.replaceAll("\\(", "d"); //$NON-NLS-1$ //$NON-NLS-2$
		name = name.replaceAll("\\)", "b"); //$NON-NLS-1$ //$NON-NLS-2$
		return name;
	}
	

}
