/*
 * (c) Copyright 2007 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from Volker Bergmann.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED CONDITIONS,
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 * HEREBY EXCLUDED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.databene.benerator;

import org.databene.commons.TreeModel;

import java.util.List;
import java.util.ArrayList;

/**
 * Generates random paths of a tree model.
 * The property 'rootIncluded' tells if the tree's root node should be included in the generated path.<br/>
 * <br/>
 * Created: 30.07.2007 18:48:23
 */
public class TreePathGenerator extends LightweightGenerator<List> {

    private TreeModel model;

    private boolean rootIncluded;

    public TreePathGenerator() {
        this(null);
    }

    public TreePathGenerator(TreeModel model) {
        super(List.class);
        this.model = model;
    }

    // properties ------------------------------------------------------------------------------------------------------

    public boolean isRootIncluded() {
        return rootIncluded;
    }

    public void setRootIncluded(boolean rootIncluded) {
        this.rootIncluded = rootIncluded;
    }

    // Generator interface ---------------------------------------------------------------------------------------------

    public void validate() {
        if (model == null)
            throw new InvalidGeneratorSetupException("model", "is null");
    }

    public List generate() {
        List path = new ArrayList();
        Object node = model.getRoot();
        if (rootIncluded)
            path.add(node);
        while (model.getChildCount(node) > 0) {
            node = randomChild(node);
            path.add(node);
        }
        return path;
    }

    private Object randomChild(Object node) {
        int childCount = model.getChildCount(node);
        int index = SimpleRandom.randomInt(0, childCount - 1);
        return model.getChild(node, index);
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    public String toString() {
        return getClass().getSimpleName() + '[' + model + ']';
    }
}
