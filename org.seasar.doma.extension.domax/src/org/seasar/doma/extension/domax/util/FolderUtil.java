package org.seasar.doma.extension.domax.util;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public final class FolderUtil {

	public static void createFolder(IFolder folder, boolean force,
			boolean local, IProgressMonitor monitor) throws CoreException {
		if (!folder.exists()) {
			IContainer parent = folder.getParent();
			if (parent instanceof IFolder) {
				createFolder((IFolder) parent, force, local, null);
			}
			folder.create(force, local, monitor);
		}
	}
}
