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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.databene.model.depend.NodeState.*;

/**
 * Orders objects by dependency.
 * @author Volker Bergmann
 * @since 0.3.04
 * @param <E>
 */
public class DependencyModel<E extends Dependent<E>> {
    
    private static final Logger logger = LoggerFactory.getLogger(DependencyModel.class);
    
    private Map<E, Node<E>> nodeMappings;
    
    public DependencyModel() {
        this.nodeMappings = new HashMap<E, Node<E>>();
    }
    
    public void addNode(E object) {
        nodeMappings.put(object, new Node<E>(object));
    }
    
    public List<E> dependencyOrderedObjects(boolean acceptingCycles) {
        // set up dependencies
        for (Node<E> node : nodeMappings.values()) {
            E subject = node.getSubject();
            for (int i = 0; i < subject.countProviders(); i++) {
                E provider = subject.getProvider(i);
                Node<E> providerNode = nodeMappings.get(provider);
                if (providerNode == null)
                    throw new IllegalStateException("Node is not part of model: " + provider);
                providerNode.addClient(node);
                node.addProvider(providerNode, subject.requiresProvider(i));
            }
        }
        
        // set up lists for processing
        List<Node<E>> heads = new ArrayList<Node<E>>();
        List<Node<E>> tails = new ArrayList<Node<E>>();
        List<Node<E>> tmp = new ArrayList<Node<E>>(nodeMappings.size());
        List<Node<E>> orderedNodes = new ArrayList<Node<E>>(nodeMappings.size());
        List<Node<E>> incompletes = new ArrayList<Node<E>>();
        
        try {
            // determine types and extract islands
            Iterator<Node<E>> iterator = nodeMappings.values().iterator();
            while (iterator.hasNext()) {
                Node<E> node = iterator.next();
                if (node.hasClients()) {
                    if (node.hasProviders()) {
                        tmp.add(node);
                    } else {
                        node.initialize();
                        heads.add(node);
                    }
                } else {
                    if (node.hasProviders()) {
                        tails.add(node);
                    } else {
                        node.initialize();
                        orderedNodes.add(node);
                    }
                }
            }
            
            // extract heads
            orderedNodes.addAll(heads);
            
            // sort remaining nodes
            while (tmp.size() > 0) {
                boolean found = extractNodes(tmp, INITIALIZABLE, orderedNodes, null);
                if (!found)
                    found = extractNodes(tmp, PARTIALLY_INITIALIZABLE, orderedNodes, incompletes);
                if (!found) {
                    if (acceptingCycles) {
                        // force one node
                        Node<E> node = findForceable(tmp);
                        logger.debug("forcing " + node); 
                        tmp.remove(node);
                        node.force();
                        orderedNodes.add(node);
                        incompletes.add(node);
                    } else
                        throw new CyclicDependencyException("Cyclic dependency in " + tmp);
                }
                postProcessNodes(incompletes);
            }
            
            if (incompletes.size() > 0)
                throw new IllegalStateException("Incomplete nodes left: " + incompletes);
    
            // extract tails
            for (Node<E> tail : tails)
                tail.initialize();
            orderedNodes.addAll(tails);
    
            // done
            if (logger.isDebugEnabled())
                logger.debug("ordered to " + orderedNodes);
            
            // map result
            List<E> result = new ArrayList<E>(orderedNodes.size());
            for (Node<E> node : orderedNodes) {
                E subject = node.getSubject();
                if (node.getState() != INITIALIZED)
                    throw new IllegalStateException("Node '" + subject 
                            + "' is expected to be in INITIALIZED state, found: " + node.getState());
                result.add(subject);
            }
            return result;
        } catch (RuntimeException e) {
            if (!(e instanceof CyclicDependencyException))
                logState(tmp);
            throw e;
        }
    }

    private void postProcessNodes(List<Node<E>> nodes) {
        Iterator<Node<E>> iterator = nodes.iterator();
        while (iterator.hasNext()) {
            Node<E> node = iterator.next();
            switch (node.getState()) {
                case PARTIALLY_INITIALIZABLE: node.initializePartially(); break;
                case INITIALIZED: node.initializePartially(); break;
                case INITIALIZABLE: node.initialize(); iterator.remove(); break;
            }
        }
    }

    private boolean extractNodes(List<Node<E>> source, NodeState requiredState, List<Node<E>> target, List<Node<E>> incompletes) {
        Iterator<Node<E>> iterator;
        boolean found = false;
        iterator = source.iterator();
        while (iterator.hasNext()) {
            Node<E> node = iterator.next();
            if (node.getState() == requiredState) {
                iterator.remove();
                switch (requiredState) {
                    case INITIALIZABLE:             node.initialize(); 
                                                    break;
                    case PARTIALLY_INITIALIZABLE:   node.initializePartially(); 
                                                    if (incompletes != null)
                                                        incompletes.add(node); 
                                                    break;
                    default: throw new IllegalArgumentException("state not supported: " + requiredState);
                }
                if (target != null)
                    target.add(node);
                found = true;
            }
        }
        return found;
    }
    
    private void logState(List<Node<E>> intermediates) {
        logger.error(intermediates.size() + " unresolved intermediates on DependencyModel error: ");
        for (Node<E> node : intermediates)
            logger.error(node.toString());
    }

    private Node<E> findForceable(List<Node<E>> candidates) {
        for (Node<E> candidate : candidates)
            if (candidate.getState() == FORCEABLE)
                return candidate;
        return candidates.get(0);
    }

/*
    private Node<E> breakCycle(List<Node<E>> remainingNodes, List<Node<E>> availableNodes, boolean hard) {
        for (Node<E> node : remainingNodes) {
            if (available(node))
            logger.warn("Cyclic dependency in " + tmpList + ". Breaking cycle by extracting " + node);
            result.add(node);
            tmpList.remove(0);
        }
    }

    private Node<E> findAvailableNode(List<Node<E>> tmpList, List<Node<E>> availableNodes) {
        for (Node<E> node : tmpList)
            if (available(node, availableNodes)) 
                return node;
        return null;
    }

    private boolean available(Node<E> candidate, List<Node<E>> availableNodes) {
        for (Node<E> usedNode : candidate.getProviders())
            if (!availableNodes.contains(usedNode))
                return false;
        return true;
    }
*/
}
