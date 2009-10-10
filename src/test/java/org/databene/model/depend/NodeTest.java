/*
 * (c) Copyright 2007-2009 by Volker Bergmann. All rights reserved.
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

package org.databene.model.depend;

import static org.databene.model.depend.NodeState.*;

import org.junit.Test;
import static junit.framework.Assert.*;

/**
 * Tests the Node class.
 * @author Volker Bergmann
 * @since 0.3.04
 */
public class NodeTest {
    
	@Test
    public void testInitialState() {
        Node<Dep>[] nodes = createDeps();
        nodes[0].assertState(INITIALIZABLE);
        nodes[1].assertState(INITIALIZABLE);
        nodes[2].assertState(INACTIVE);
    }

	@Test
    public void testInitialization() {
        Node<Dep>[] nodes = createDeps();
        nodes[0].initialize();
        nodes[0].assertState(INITIALIZED);
        nodes[2].assertState(FORCEABLE);
        
        try {
            nodes[2].initialize();
            fail("Exception expected");
        } catch (IllegalStateException e) {
            // expected
        }
        
        nodes[1].initialize();
        nodes[1].assertState(INITIALIZED);
        nodes[2].assertState(INITIALIZABLE);
    
        nodes[2].initialize();
        nodes[2].assertState(INITIALIZED);
    }

    @SuppressWarnings("unchecked")
    private Node<Dep>[] createDeps() {
        Dep da1 = new Dep("a1");
        Dep da2 = new Dep("a2");
        Dep db  = new Dep("b", da1, da2);
        
        Node<Dep> na1 = new Node<Dep>(da1);
        Node<Dep> na2 = new Node<Dep>(da2);
        Node<Dep> nb = new Node<Dep>(db).addProvider(na1, true).addProvider(na2, true);
        
        return new Node[] { na1, na2, nb };
    }

}
