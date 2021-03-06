/*******************************************************************************
 * Copyright (c) 2016,2019 iSencia Belgium NV.
 *  
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Erwin De Ley - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.triquetrum.workflow.editor.features;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.triquetrum.workflow.model.Relation;

public class ConnectionDeleteFeature extends TriqDefaultDeleteFeature {

  public ConnectionDeleteFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canDelete(IDeleteContext context) {
    PictogramElement connectionPE = context.getPictogramElement();
    if (connectionPE instanceof Connection) {
      Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
      return (bo instanceof Relation);
    } else {
      return false;
    }
  }

  @Override
  protected void deleteBusinessObject(Object bo) {
    Relation relation = (Relation) bo;
    if (relation != null && !relation.isConnected()) {
      // TODO check if/how we might want keep an unconnected Vertex around after all links were deleted/removed
      // I guess with the check above, such vertex would be deleted as well at the moment the last connection/link is removed/deleted.
      EcoreUtil.delete(relation, true);
    }
  }
}
