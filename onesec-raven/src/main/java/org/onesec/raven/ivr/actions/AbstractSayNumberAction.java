/*
 *  Copyright 2011 Mikhail Titov.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.onesec.raven.ivr.actions;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.onesec.raven.ivr.IvrEndpointConversation;
import org.onesec.raven.ivr.impl.AudioFileNode;
import org.onesec.raven.ivr.impl.IvrUtils;
import org.raven.tree.Node;
import org.raven.tree.ResourceManager;

/**
 *
 * @author Mikhail Titov
 */
public abstract class AbstractSayNumberAction extends AsyncAction
{
    private final Node numbersNode;
    private final long pauseBetweenWords;
    private final long pauseBetweenNumbers;
    private final ResourceManager resourceManager;

    public AbstractSayNumberAction(String actionName, Node numbersNode, long pauseBetweenWords, 
        long pauseBetweenNumbers, ResourceManager resourceManager)
    {
        super(actionName);
        this.numbersNode = numbersNode;
        this.pauseBetweenWords = pauseBetweenWords;
        this.pauseBetweenNumbers = pauseBetweenNumbers;
        this.resourceManager = resourceManager;
    }

    public boolean isFlowControlAction() {
        return false;
    }

    protected abstract List<List> formWords(IvrEndpointConversation conversation);

    @Override
    protected void doExecute(IvrEndpointConversation conversation) throws Exception
    {
        List<List> numbers = formWords(conversation);
        if (numbers==null || numbers.isEmpty()) {
            if (logger.isWarnEnabled())
                logger.warn("No number(s) to say");            
        } else
            for (List number: numbers) {
                sayNumber(number);
                TimeUnit.MILLISECONDS.sleep(pauseBetweenNumbers);
            }
    }
    
    private void sayNumber(List words) throws Exception {
        if (words==null || words.isEmpty())
            return;
        
        int i=0;
        AudioFileNode[] audioSources = new AudioFileNode[words.size()];
        for (Object word: words) {
            AudioFileNode audio = null;
            if (word instanceof AudioFileNode)
                audio = (AudioFileNode) word;
            else if (word instanceof String) {
                Node child = resourceManager.getResource(numbersNode, (String)word, null);
                if (!(child instanceof AudioFileNode)) {
                    if (logger.isErrorEnabled())
                        logger.error(String.format(
                                "Can not say the number because of not found " +
                                "the AudioFileNode (%s) node in the (%s) numbers node"
                                , word, numbersNode.getPath()));
                    return;
                } else
                    audio = (AudioFileNode) child;
            }            
            audioSources[i++] = audio;
        }

        for (AudioFileNode audioFile: audioSources) {
            IvrUtils.playAudioInAction(this, conversation, audioFile);
            if (hasCancelRequest())
                return;
            TimeUnit.MILLISECONDS.sleep(pauseBetweenWords);
        }
    }
}
