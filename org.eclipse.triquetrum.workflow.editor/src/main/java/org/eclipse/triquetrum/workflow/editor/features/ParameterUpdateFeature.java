/*******************************************************************************
 * Copyright (c) 2015 iSencia Belgium NV.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Erwin De Ley - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.triquetrum.workflow.editor.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.triquetrum.workflow.model.Parameter;

/**
 *
 */
public class ParameterUpdateFeature extends AbstractUpdateFeature {

  public ParameterUpdateFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canUpdate(IUpdateContext context) {
    String boCategory = Graphiti.getPeService().getPropertyValue(context.getPictogramElement(), "__BO_CATEGORY");
    return ("PARAMETER".equals(boCategory));
  }

  @Override
  public IReason updateNeeded(IUpdateContext context) {
    PictogramElement pictogramElement = context.getPictogramElement();
    Object bo = getBusinessObjectForPictogramElement(pictogramElement);
    boolean parameterChanged = false;
    Parameter p = null;
    if (bo instanceof Parameter && pictogramElement instanceof Shape) {
      p = (Parameter) bo;
      Shape shape = (Shape) pictogramElement;
      String boCategory = Graphiti.getPeService().getPropertyValue(shape, "__BO_CATEGORY");
      if ("PARAMETER".equalsIgnoreCase(boCategory)) {
        // parameters can not change name, only the value can change
        String boValue = Graphiti.getPeService().getPropertyValue(shape, "__BO_VALUE");
        parameterChanged = !p.getExpression().equals(boValue);
      }
    }
    if (parameterChanged) {
      context.putProperty("PARAMETER_CHANGED", p.getName());
      return Reason.createTrueReason("Parameter change");
    } else {
      return Reason.createFalseReason();
    }
  }

  @Override
  public boolean update(IUpdateContext context) {
    boolean result = false;
    PictogramElement pictogramElement = context.getPictogramElement();
    Object bo = getBusinessObjectForPictogramElement(pictogramElement);
    if (bo instanceof Parameter && pictogramElement instanceof Shape) {
      Parameter param = (Parameter) bo;
      Shape shape = (Shape) pictogramElement;
      String boCategory = Graphiti.getPeService().getPropertyValue(shape, "__BO_CATEGORY");
      if ("PARAMETER".equals(boCategory)) {
        Text text = (Text) shape.getGraphicsAlgorithm();
        String pName = param.getName();
        String pVal = param.getExpression();
        pName = (pName.length() > 12) ? pName.substring(0, 12) : pName;
        pVal = (pVal.length() > 12) ? pVal.substring(0, 12) : pVal;
        text.setValue(pName + " : " + pVal);
        Graphiti.getPeService().setPropertyValue(shape, "__BO_VALUE", param.getExpression());
        result = true;
      }
    }
    return result;
  }
}