package org.seasar.doma.extension.domax.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.seasar.doma.extension.domax.Logger;

public final class JavaProjectUtil {

	public static List<IResource> getSourceFolders(IJavaProject javaProject) {
		List<IResource> results = new ArrayList<IResource>();
		try {
			for (IPackageFragmentRoot root : javaProject
					.getPackageFragmentRoots()) {
				if (root.getKind() == IPackageFragmentRoot.K_SOURCE) {
					results.add(root.getCorrespondingResource());
				}
			}
		} catch (JavaModelException e) {
			Logger.error(e);
		}
		return results;
	}
}
