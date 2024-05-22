package com.uwisiesmic.drrcdatawarehouse;

import org.eclipse.swt.widgets.Display;

import edu.illinois.ncsa.ergo.gis.GISDatasetRepository;
import edu.illinois.ncsa.ergo.gis.datasets.factories.DatasetFactory;
import edu.illinois.ncsa.ergo.gis.exceptions.DataIngestException;
import edu.illinois.ncsa.ergo.gis.managers.RepositoryManager;
import edu.illinois.ncsa.ergo.gis.util.FactoryUtils;
import ncsa.tools.common.eclipse.descriptors.exceptions.UnknownExtensionException;

public class DataImportToWebApp {
	{
		RepositoryManager repoMgr = RepositoryManager.getInstance();
		repo = (GISDatasetRepository) repoMgr.getRepository(repoId);

		try {
			DatasetFactory factory = FactoryUtils.getFactoryForDataFormat(properties.getDataFormat());
			factory.prepareInitializedProperties(properties, selectedDataStore, monitor);

			repo.ingest(properties, selectedDataStore, sourceFiles, monitor);
		} catch (DataIngestException ex) {
			hasImportFailed = true;
			logger.error("Could not ingest the data", ex); //$NON-NLS-1$
		} catch (UnknownExtensionException ex) {
			hasImportFailed = true;
			logger.error("Could not ingest the data", ex); //$NON-NLS-1$
		} catch (Throwable t) {
			hasImportFailed = true;
			logger.error("Could not ingest the data", t); //$NON-NLS-1$
		}

		Display.getDefault().asyncExec(new Runnable() {
			public void run()
			{
				if (hasImportFailed) {
					displayImportError();
				}

				if (repoView != null) {
					repoView.refresh();
				}
			}
		});
	}
};
}
