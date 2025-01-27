/*
 * Copyright 2015 The FireNio Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package test.io.lenthvalue;

import com.firenio.codec.lengthvalue.LengthValueCodec;
import com.firenio.codec.lengthvalue.LengthValueFrame;
import com.firenio.common.Util;
import com.firenio.component.ChannelAcceptor;
import com.firenio.component.ChannelManagerListener;
import com.firenio.component.Frame;
import com.firenio.component.IoEventHandle;
import com.firenio.component.LoggerChannelOpenListener;
import com.firenio.component.Channel;

public class TestLengthValueBroadcastServer {

    public static void main(String[] args) throws Exception {

        final ChannelManagerListener channelManagerListener = new ChannelManagerListener();

        IoEventHandle eventHandleAdaptor = new IoEventHandle() {

            @Override
            public void accept(Channel ch, Frame frame) throws Exception {
                LengthValueFrame f = (LengthValueFrame) frame;
                frame.write("yes server already accept your message:", ch);
                frame.write(f.getStringContent(), ch);
                ch.writeAndFlush(f);
            }
        };

        ChannelAcceptor context = new ChannelAcceptor(8300);
        context.addChannelEventListener(new LoggerChannelOpenListener());
        context.addChannelEventListener(new SetOptionListener());
        context.addChannelEventListener(channelManagerListener);
        context.setIoEventHandle(eventHandleAdaptor);
        context.addProtocolCodec(new LengthValueCodec());
        context.bind();

        Util.exec(new Runnable() {

            @Override
            public void run() {
                for (; ; ) {
                    Util.sleep(1000);
                    LengthValueFrame frame = new LengthValueFrame();
                    frame.setContent("broadcast msg ........".getBytes());
                    try {
                        channelManagerListener.broadcast(frame);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
