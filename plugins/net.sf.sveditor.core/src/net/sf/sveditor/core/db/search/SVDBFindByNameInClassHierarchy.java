/****************************************************************************
 * Copyright (c) 2008-2010 Matthew Ballance and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Ballance - initial implementation
 ****************************************************************************/


package net.sf.sveditor.core.db.search;

import java.util.ArrayList;
import java.util.List;

import net.sf.sveditor.core.db.ISVDBItemBase;
import net.sf.sveditor.core.db.ISVDBNamedItem;
import net.sf.sveditor.core.db.ISVDBScopeItem;
import net.sf.sveditor.core.db.SVDBItemType;
import net.sf.sveditor.core.db.SVDBModIfcClassDecl;
import net.sf.sveditor.core.db.SVDBParamPort;
import net.sf.sveditor.core.db.SVDBTaskFuncScope;
import net.sf.sveditor.core.db.index.ISVDBIndexIterator;
import net.sf.sveditor.core.log.LogFactory;
import net.sf.sveditor.core.log.LogHandle;

public class SVDBFindByNameInClassHierarchy {
	private ISVDBIndexIterator				fIndexIterator;
	private LogHandle						fLog;
	private ISVDBFindNameMatcher			fMatcher;
	private SVDBFindDefaultNameMatcher		fDefaultMatcher;
	
	
	public SVDBFindByNameInClassHierarchy(
			ISVDBIndexIterator 		index_it,
			ISVDBFindNameMatcher	matcher) {
		fIndexIterator = index_it;
		fMatcher = matcher;
		fDefaultMatcher = new SVDBFindDefaultNameMatcher();
		fLog = LogFactory.getLogHandle("FindByNameInClassHierarchy");
	}
	
	public List<ISVDBItemBase> find(
			ISVDBScopeItem 		scope, 
			String 				id,
			SVDBItemType	...	types) {
		List<ISVDBItemBase> ret = new ArrayList<ISVDBItemBase>();
		
		fLog.debug("--> find(" + ((scope != null)?
				((ISVDBNamedItem)scope).getName():null) + " \"" + id + "\")");
		for (SVDBItemType t : types) {
			fLog.debug("    TYPE " + t);
		}
		
		if (scope != null && 
				((ISVDBNamedItem)scope).getName() != null && 
				((ISVDBNamedItem)scope).getName().indexOf("::") != -1) {
			// Looks like an extern function
			String clsname = ((ISVDBNamedItem)scope).getName().substring(0, 
					((ISVDBNamedItem)scope).getName().indexOf("::"));
			
			SVDBFindNamedModIfcClassIfc finder = new SVDBFindNamedModIfcClassIfc(fIndexIterator);
			List<SVDBModIfcClassDecl> result = finder.find(clsname);
			
			if (result.size() > 0) {
				scope = result.get(0);
			}
		} else {
			// Assume we're in a containing scope
			while (scope != null && 
					scope.getType() != SVDBItemType.Class &&
					scope.getType() != SVDBItemType.Struct &&
					scope.getType() != SVDBItemType.Covergroup &&
					scope.getType() != SVDBItemType.Coverpoint) {
				fLog.debug("Searching up-scope (current is " + scope.getType() + 
						" " + ((ISVDBNamedItem)scope).getName() + ")");
				if (scope.getType() == SVDBItemType.Task || scope.getType() == SVDBItemType.Function) {
					findTFParamsLocals(ret, (SVDBTaskFuncScope)scope, id, types);
				}
				scope = scope.getParent();
			}
		}
		
		if (scope == null) {
			fLog.debug("Failed to find Class/Struct scope");
			fLog.debug("<-- find(\"" + id + "\") returns " + ret.size() + " results");
			return ret;
		}
		
		// Now, search through the scope and the class hierarchy
		while (scope != null) {
			fLog.debug("Searching scope \"" + ((ISVDBNamedItem)scope).getName() + "\"");
			for (ISVDBItemBase it : scope.getItems()) {
				boolean matches = (types.length == 0);
				
				for (SVDBItemType type : types) {
					if (it.getType() == type) {
						matches = true;
						break;
					}
				}

				if (matches) {
					if (fMatcher.match((ISVDBNamedItem)it, id)) {
						ret.add(it);
					}
				}
			}

			// Always match exact
			SVDBFindSuperClass finder = new SVDBFindSuperClass(fIndexIterator, fDefaultMatcher);
			if (((SVDBModIfcClassDecl)scope).getSuperClass() != null) {
				scope = finder.find((SVDBModIfcClassDecl)scope);
				if (scope != null) {
					fLog.debug("Find super-class \"" + 
						((SVDBModIfcClassDecl)scope).getSuperClass() + "\" returns " + scope);
				}
			} else {
				fLog.debug("No super-class");
				scope = null;
			}
		}
		
		fLog.debug("<-- find(\"" + id + "\") returns " + ret.size() + " results");
		return ret;
	}
	
	private void findTFParamsLocals(
			List<ISVDBItemBase>	items,
			SVDBTaskFuncScope 	scope, 
			String 				id,
			SVDBItemType	...	types) {
		boolean matches = (types.length == 0);

		for (SVDBParamPort it : scope.getParams()) {
			for (SVDBItemType type : types) {
				if (it.getType() == type) {
					matches = true;
					break;
				}
			}
			
			if (matches) {
				if (fMatcher.match(it, id)) {
					items.add(it);
				}
			}
		}
		
		for (ISVDBItemBase it : scope.getItems()) {
			for (SVDBItemType type : types) {
				if (it.getType() == type) {
					matches = true;
					break;
				}
			}
			
			if (matches) {
				if (fMatcher.match((ISVDBNamedItem)it, id)) {
					items.add(it);
				}
			}
		}
	}
}
