package edu.psu.ems.gis2defnode;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {
	
	public final static String ID = "edu.psu.ems.gis2defnode.Perspective";

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);
		
//		layout.addStandaloneView(View.ID,  false, IPageLayout.TOP, 1.0f, editorArea);
		layout.addStandaloneView(MapView.ID,  false, IPageLayout.TOP, 1.0f, editorArea);
//		layout.addStandaloneView(View.ID,true, IPageLayout.TOP,IPageLayout.RATIO_MAX, IPageLayout.ID_EDITOR_AREA);
	}

}
