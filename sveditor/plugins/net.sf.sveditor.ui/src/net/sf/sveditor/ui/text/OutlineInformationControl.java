package net.sf.sveditor.ui.text;

/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Armond Paiva - repurposed from JDT for SVEditor Objects Quick View
 *     Armond Paiva - repurposed from Objects Quick View to Outline Quick View
 *******************************************************************************/

import net.sf.sveditor.core.db.ISVDBItemBase;
import net.sf.sveditor.core.db.SVDBFile;
import net.sf.sveditor.ui.editor.SVEditor;
import net.sf.sveditor.ui.svcp.SVDBDecoratingLabelProvider;
import net.sf.sveditor.ui.svcp.SVTreeContentProvider;
import net.sf.sveditor.ui.svcp.SVTreeLabelProvider;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * Show outline in light-weight control.
 *
 * @since 2.1
 */
public class OutlineInformationControl extends AbstractInformationControl {

	/**
	 * Creates a new system verilog outline information control.
	 *
	 * @param parent
	 * @param shellStyle
	 * @param treeStyle
	 * @param commandId
	 */
	public OutlineInformationControl(Shell parent, int shellStyle, int treeStyle, String commandId) {
		super(parent, shellStyle, treeStyle, commandId, true);
	}

	
	protected FilteredTree fObjectTree ;
	protected PatternFilter fPatternFilter ;
	protected TreeViewer fTreeViewer ;
	protected SVTreeContentProvider fContentProvider ;
	protected SVDBFile fSVDBFile ;
	protected SVEditor fEditor ;

	@Override
	public void setFocus() {
	  fObjectTree.getFilterControl().setFocus() ;
	};
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected TreeViewer createTreeViewer(Composite parent, int style) {
		
		fPatternFilter = new PatternFilter() ;

		fObjectTree = new FilteredTree(parent, SWT.H_SCROLL, fPatternFilter, true) ;
		fTreeViewer = fObjectTree.getViewer() ;
		fContentProvider = new SVTreeContentProvider() ;
		
		final Tree tree = fTreeViewer.getTree();
		
		GridData gd= new GridData(GridData.FILL_BOTH);
		gd.heightHint= tree.getItemHeight() * 20 ; // Initial height of dialog
		gd.widthHint = 600 ;					   // Initial width of dialog
		tree.setLayoutData(gd);
		
		fTreeViewer.setContentProvider(fContentProvider) ;
		fTreeViewer.setLabelProvider(new SVDBDecoratingLabelProvider(new SVTreeLabelProvider()));
		fTreeViewer.setComparator(new ViewerComparator()) ;
		fTreeViewer.setComparer(new IElementComparer() {
			public int hashCode(Object element) {
				return element.hashCode();
			}
			public boolean equals(Object a, Object b) {
				// Just do a simple compare
				return (a == b);
			}
		});
		
		fTreeViewer.setInput(fSVDBFile) ;
		fTreeViewer.expandAll() ;
		
		fTreeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection sel = (IStructuredSelection)event.getSelection();
				if (sel.getFirstElement() instanceof ISVDBItemBase) {
					ISVDBItemBase n = (ISVDBItemBase)sel.getFirstElement();
					fEditor.setSelection(n, false) ;
					close() ;
				}
			}
		});		

		return fTreeViewer;
	}


	@Override
	protected String getId() {
		return "net.sf.sveditor.ui.text.QuickOutline"; //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setInput(Object information) { 
		if(information == null) {
			fEditor = null ;
			fSVDBFile = null ;
		} else if(information instanceof SVEditor) {
			fEditor = (SVEditor)information ;
			fSVDBFile = fEditor.getSVDBFile() ;
			fTreeViewer.setInput(fSVDBFile) ;
			fTreeViewer.expandAll() ;
		}
	}

}
