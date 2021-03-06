/****************************************************************************
 * Copyright (c) 2008-2014 Matthew Ballance and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Ballance - initial implementation
 ****************************************************************************/


package net.sf.sveditor.core.tests.profile;

import java.io.File;

import net.sf.sveditor.core.db.index.ISVDBIndex;
import net.sf.sveditor.core.db.index.SVDBIndexCollection;
import net.sf.sveditor.core.db.index.SVDBIndexRegistry;
import net.sf.sveditor.core.db.index.old.SVDBLibPathIndexFactory;
import net.sf.sveditor.core.tests.TestIndexCacheFactory;
import net.sf.sveditor.core.tests.utils.TestUtils;

public class ProfileIndexLoad {
	
	File							fTmpDir;

	
	public void testLoadOVM(String ovm_home) {
		String pname = "testLoadOVM";
		SVDBIndexCollection mgr = new SVDBIndexCollection(pname);
		SVDBIndexRegistry rgy = new SVDBIndexRegistry();
		
//		rgy.init(TestIndexCacheFactory.instance(fTmpDir));
		rgy.init(TestIndexCacheFactory.instance());
		
		String lib_path = ovm_home + "/src/ovm_pkg.sv";
		
		System.out.println("Library Path: " + lib_path);
		SVDBLibPathIndexFactory f = new SVDBLibPathIndexFactory();
		// TODO: provide cache
		ISVDBIndex index = f.createSVDBIndex(pname, lib_path, null, null);
		mgr.addLibraryPath(index);
	
		/** TODO:
		ISVDBItemIterator item_it = mgr.getItemIterator(new NullProgressMonitor());
		
		int count = 0;
		while (item_it.hasNext()) {
			item_it.nextItem();
			count++;
		}
		
		System.out.println("" + count + " items");
		 */
	}
	
	public static final void main(String args[]) {
		
		ProfileIndexLoad t = new ProfileIndexLoad();
		
		t.fTmpDir = TestUtils.createTempDir();
		
		long start_time = System.currentTimeMillis();
		try {
			t.testLoadOVM(args[0]);
		} finally {
			long end_time = System.currentTimeMillis();
			
			System.out.println("Parse Time: " + (end_time - start_time));
			TestUtils.delete(t.fTmpDir);
		}
	}

}
