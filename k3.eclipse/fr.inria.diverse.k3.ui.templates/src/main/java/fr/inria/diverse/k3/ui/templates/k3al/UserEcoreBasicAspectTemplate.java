/*******************************************************************************
 *  Copyright (c) 2000, 2007 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package fr.inria.diverse.k3.ui.templates.k3al;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.ui.dialogs.WorkspaceResourceDialog;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleException;

import fr.inria.diverse.commons.eclipse.pde.classpath.ClasspathHelper;
import fr.inria.diverse.commons.eclipse.pde.wizards.pages.pde.ui.BaseProjectWizardFields;
import fr.inria.diverse.commons.eclipse.pde.wizards.pages.pde.ui.templates.AbstractStringWithButtonOption;
import fr.inria.diverse.commons.eclipse.pde.wizards.pages.pde.ui.templates.TemplateOption;
import fr.inria.diverse.k3.ui.templates.Activator;
import fr.inria.diverse.k3.ui.templates.IHelpContextIds;
import fr.inria.diverse.k3.ui.templates.K3TemplateMessages;
import fr.inria.diverse.k3.ui.templates.K3TemplateSection;
import fr.inria.diverse.k3.ui.tools.ManifestChanger;
import fr.inria.diverse.k3.ui.wizards.pages.NewK3ProjectWizardFields;
import fr.inria.diverse.k3.ui.wizards.pages.NewK3ProjectWizardFields.KindsOfProject;

public class UserEcoreBasicAspectTemplate extends K3TemplateSection {
	public static final String KEY_ASPECTCLASS_POSTFIX = "aspectClassPostfix"; //$NON-NLS-1$
	public static final String ASPECTCLASS_POSTFIX = "Aspect"; //$NON-NLS-1$
	public static final String KEY_ASPECTFILE_NAME = "aspectFileName"; //$NON-NLS-1$
	public static final String ASPECTFILE_NAME = "MyAspects"; //$NON-NLS-1$
	public static final String KEY_ECOREFILE_PATH = "ecoreFilePath"; //$NON-NLS-1$
	
	protected static final List<String> FILE_EXTENSIONS = Arrays.asList(new String [] { "ecore" });

	NewK3ProjectWizardFields _data;
	
	/**
	 * Constructor for HelloWorldTemplate.
	 */
	public UserEcoreBasicAspectTemplate() {
		setPageCount(1);
		createOptions();
	}

	/** 
	 * used to retrieve the template folder
	 */
	public String getSectionId() {
		return "userEcoreBasicAspect"; //$NON-NLS-1$
	}

	/*
	 * @see ITemplateSection#getNumberOfWorkUnits()
	 */
	public int getNumberOfWorkUnits() {
		return super.getNumberOfWorkUnits() + 1;
	}

	protected void createOptions() {
		addOption(KEY_PACKAGE_NAME, K3TemplateMessages.UserEcoreBasicAspectTemplate_packageName, (String) null, 0);
		addOption(KEY_ASPECTFILE_NAME, K3TemplateMessages.UserEcoreBasicAspectTemplate_aspectFileName, ASPECTFILE_NAME, 0);
		addOption(KEY_ASPECTCLASS_POSTFIX, K3TemplateMessages.UserEcoreBasicAspectTemplate_aspectClassPostfix, ASPECTCLASS_POSTFIX, 0);
		//addOption(KEY_ECOREFILE_LOCATION, K3TemplateMessages.UserEcoreBasicAspectTemplate_ecoreFileLocation, (String) null, 0);
		TemplateOption ecoreLocationOption  = new AbstractStringWithButtonOption(this, KEY_ECOREFILE_PATH, K3TemplateMessages.UserEcoreBasicAspectTemplate_ecoreFilePath) {
			@Override
			public String doSelectButton() {
				final IWorkbenchWindow workbenchWindow = PlatformUI
						.getWorkbench().getActiveWorkbenchWindow();
				Object selection = null;
				if (workbenchWindow.getSelectionService().getSelection() instanceof IStructuredSelection) {
					selection = ((IStructuredSelection) workbenchWindow
							.getSelectionService().getSelection())
							.getFirstElement();
				}
				final IFile selectedEcoreFile = selection != null
						&& selection instanceof IFile
						&& FILE_EXTENSIONS.contains(((IFile) selection)
								.getFileExtension()) ? (IFile) selection : null;
				ViewerFilter viewerFilter = new ViewerFilter() {
					@Override
					public boolean select(Viewer viewer, Object parentElement,
							Object element) {
						if (element instanceof IFile) {
							IFile file = (IFile) element;
							return FILE_EXTENSIONS.contains(file
									.getFileExtension())
									&& (selectedEcoreFile == null || !selectedEcoreFile
											.getFullPath().equals(
													file.getFullPath()));
						}
						return true;
					}
				};
				final IFile[] files = WorkspaceResourceDialog
						.openFileSelection(workbenchWindow.getShell(), null,
								"Select ecore", true, null,
								Collections.singletonList(viewerFilter));
				if (files.length > 0) {
					UserEcoreBasicAspectTemplate.this._data.ecoreIFile = files[0];
					//txtPathEcore.setText(files[i].getFullPath().toOSString());
					//UserEcoreBasicAspectTemplate.this._data.ecoreProjectPath = files[0].getProject().getFullPath().toOSString();
					return files[0].getFullPath().toOSString();
				}

				return null;
			}
		};
		registerOption(ecoreLocationOption, (String) null, 0);
	}

	public void addPages(Wizard wizard) {
		WizardPage page = createPage(0, IHelpContextIds.TEMPLATE_ECORE_ASPECT);
		page.setTitle(K3TemplateMessages.UserEcoreBasicAspectTemplate_title);
		page.setDescription(K3TemplateMessages.UserEcoreBasicAspectTemplate_desc);
		wizard.addPage(page);
		markPagesAdded();
	}

	public boolean isDependentOnParentWizard() {
		return true;
	}

	protected void initializeFields(BaseProjectWizardFields data) {
		// save reference to content for later use 
		_data = (NewK3ProjectWizardFields)data;
		// initialize values according to previous pages content or o
		String packageName = getFormattedPackageName(_data.projectName);
		initializeOption(KEY_PACKAGE_NAME, packageName);
		if(_data.ecoreIFile != null){
			initializeOption(KEY_ECOREFILE_PATH,_data.ecoreIFile.getFullPath().toOSString());
		} 
		
	}


	public String getUsedExtensionPoint() {
		return "org.eclipse.ui.actionSets"; //$NON-NLS-1$
	}


	/* (non-Javadoc)
	 * @see org.eclipse.pde.ui.templates.ITemplateSection#getFoldersToInclude()
	 */
	public String[] getNewFiles() {
		return new String[] {"icons/"}; //$NON-NLS-1$
	}

	
	
	
	/* (non-Javadoc)
	 * @see fr.inria.diverse.k3.ui.templates.K3TemplateSection#generateFiles(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void generateFiles(IProgressMonitor monitor) throws CoreException {
		updateDataFromOptions();
		super.generateFiles(monitor);
		
		k3.language.aspectgenerator.AspectGenerator.aspectGenerate (
				new ArrayList<String>(),
				"File:///"+_data.projectLocation,
				_data.projectName,
				null, //_data.operationName,
				"File:///"+_data.ecoreIFile.getLocation().toOSString(), 
				new ArrayList<String>(), //_data.listNewClass, (no need to create empty classes)
				new ArrayList<String>()//_data.operationParams
				);
		
		// now also fix the project configuration
		
		// If this is a plugin
		if(_data.kindsOfProject == KindsOfProject.PLUGIN){
			ManifestChanger manifestChanger;
			try {
				manifestChanger = new ManifestChanger(project.getFile("META-INF/MANIFEST.MF"));
				manifestChanger.addPluginDependency(_data.ecoreIFile.getProject().getName(), "0.0.0", false, true);
				manifestChanger.writeManifest(project.getFile("META-INF/MANIFEST.MF"));
			} catch (IOException | BundleException e) {
				Activator.logErrorMessage(e.getMessage(), e);
			}
		}
		else if(_data.kindsOfProject == KindsOfProject.STANDALONE){
			// must update the build path
			ClasspathHelper.addEntry(project, JavaCore.newProjectEntry(_data.ecoreIFile.getProject().getFullPath()), monitor);
		}
		else if(_data.kindsOfProject == KindsOfProject.MAVEN){

			ClasspathHelper.addEntry(project, JavaCore.newProjectEntry(_data.ecoreIFile.getProject().getFullPath()), monitor);
			// TODO must update the pom.xml
		}

		
				 
	}

	
	public void updateDataFromOptions() {
		// for convenience, copy the appropriate values from the wizard options into the data 
		_data.namePackage = (String) this.getValue(KEY_PACKAGE_NAME);
		
	}
}