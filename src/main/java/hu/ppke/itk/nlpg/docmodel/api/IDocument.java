/*******************************************************************************
 * Copyright (c) 2011 György Orosz, Attila Novák, Balázs Indig
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     György Orosz - initial API and implementation
 ******************************************************************************/
package hu.ppke.itk.nlpg.docmodel.api;

import java.util.List;

/**
 * Implementors should represent a document, which has sentences.
 * 
 * @author György Orosz
 * 
 */
public interface IDocument extends IDocElementContainer<IParagraph> {

	/**
	 * Get the sentences in the document.
	 * 
	 * @return List of the sentences stored in the document
	 */
	public List<ISentence> getSentences();
}
