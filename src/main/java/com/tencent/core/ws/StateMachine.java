package com.tencent.core.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 状态机
 */
public class StateMachine {
    private static Logger logger = LoggerFactory.getLogger(StateMachine.class);
    protected State state = State.STATE_INIT;

    public enum State {
        /**
         * 错误状态
         */
        STATE_FAIL(-1) {
            @Override
            public void checkStart() {
                throw new RuntimeException("can't start,current state is " + this);
            }

            @Override
            public void checkSend() {
                throw new RuntimeException("can't send,current state is " + this);
            }

            @Override
            public void checkStop() {
                throw new RuntimeException("can't stop,current state is " + this);
            }

            @Override
            public State init() {
                return this;
            }

            @Override
            public State start() {
                return this;
            }

            @Override
            public State fail() {
                return this;
            }

            @Override
            public State complete() {
                return this;
            }

            @Override
            public State closed() {
                return this;
            }

            @Override
            public State send() {
                return this;
            }

            @Override
            public State stopSend() {
                return this;
            }
        },
        /**
         * 初始状态
         */
        STATE_INIT(0) {
            @Override
            public void checkStart() {

            }

            @Override
            public void checkSend() {
                throw new RuntimeException("can't send,current state is " + this);
            }

            @Override
            public void checkStop() {
                throw new RuntimeException("can't stop,current state is " + this);
            }

            @Override
            public State init() {
                return this;
            }

            @Override
            public State start() {
                return STATE_START;
            }

            @Override
            public State fail() {
                return this;
            }

            @Override
            public State complete() {
                return this;
            }

            @Override
            public State closed() {
                return this;
            }

            @Override
            public State send() {
                return this;
            }

            @Override
            public State stopSend() {
                return this;
            }
        },
        /**
         * 已连接状态
         */
        STATE_START(1) {
            @Override
            public void checkSend() {

            }

            @Override
            public void checkStart() {
                throw new RuntimeException("can't start,current state is " + this);
            }

            @Override
            public void checkStop() {
                throw new RuntimeException("can't stop,current state is " + this);
            }

            @Override
            public State init() {
                return this;
            }

            @Override
            public State start() {
                return this;
            }

            @Override
            public State fail() {
                return STATE_FAIL;
            }

            @Override
            public State complete() {
                return STATE_COMPLETE;
            }

            @Override
            public State closed() {
                return STATE_CLOSED;
            }

            @Override
            public State send() {
                return STATE_SEND;
            }

            @Override
            public State stopSend() {
                return STATE_STOP_SENT;
            }
        },
        /**
         * 准备发送音频
         */
        STATE_SEND(2) {
            @Override
            public void checkSend() {
                return;
            }

            @Override
            public void checkStart() {
                throw new RuntimeException("can't start,current state is " + this);
            }

            @Override
            public void checkStop() {
                return;
            }

            @Override
            public State init() {
                return this;
            }

            @Override
            public State start() {
                return this;
            }

            @Override
            public State fail() {
                return STATE_FAIL;
            }

            @Override
            public State complete() {
                return STATE_COMPLETE;
            }

            @Override
            public State closed() {
                return STATE_CLOSED;
            }

            @Override
            public State send() {
                return this;
            }

            @Override
            public State stopSend() {
                return STATE_STOP_SENT;
            }
        },
        /**
         * 语音发送完毕,识别结束指令已发送
         */
        STATE_STOP_SENT(3) {
            @Override
            public void checkSend() {
                throw new RuntimeException("only STATE_REQUEST_CONFIRMED can send,current state is " + this);
            }

            @Override
            public void checkStart() {
                throw new RuntimeException("can't start,current state is " + this);
            }

            @Override
            public void checkStop() {

            }

            @Override
            public State init() {
                return this;
            }

            @Override
            public State start() {
                return this;
            }

            @Override
            public State fail() {
                return STATE_FAIL;
            }

            @Override
            public State complete() {
                return STATE_COMPLETE;
            }

            @Override
            public State closed() {
                return STATE_CLOSED;
            }


            @Override
            public State send() {
                return this;
            }

            @Override
            public State stopSend() {
                return STATE_STOP_SENT;
            }
        },

        /**
         * 收到全部识别结果,识别结束
         */
        STATE_COMPLETE(4) {
            @Override
            public void checkSend() {
                //开启静音监测时,服务端可能提前结束
                logger.warn("task is completed before sending binary");
            }

            @Override
            public void checkStart() {
                return;
            }

            @Override
            public void checkStop() {
                //开启静音监测时,服务端可能提前结束
                logger.warn("task is completed before sending stop command");
            }

            @Override
            public State init() {
                return this;
            }

            @Override
            public State start() {
                return this;
            }

            @Override
            public State fail() {
                return STATE_FAIL;
            }

            @Override
            public State complete() {
                return this;
            }

            @Override
            public State closed() {
                return STATE_CLOSED;
            }

            @Override
            public State send() {
                return this;
            }

            @Override
            public State stopSend() {
                return this;
            }
        },
        /**
         * 连接关闭
         */
        STATE_CLOSED(6) {
            @Override
            public void checkSend() {
                throw new RuntimeException("can't send,current state is " + this);
            }

            @Override
            public void checkStart() {
                throw new RuntimeException("can't start,current state is " + this);
            }

            @Override
            public void checkStop() {
                throw new RuntimeException("can't stop,current state is " + this);
            }

            @Override
            public State init() {
                return this;
            }

            @Override
            public State start() {
                return this;
            }

            @Override
            public State fail() {
                return this;
            }

            @Override
            public State complete() {
                return this;
            }

            @Override
            public State closed() {
                return this;
            }

            @Override
            public State send() {
                return this;
            }

            @Override
            public State stopSend() {
                return this;
            }
        };

        int value;

        public abstract void checkSend();

        public abstract void checkStart();

        public abstract void checkStop();

        public abstract State init();

        public abstract State start();

        public abstract State fail();

        public abstract State complete();

        public abstract State closed();

        public abstract State send();

        public abstract State stopSend();

        State(int value) {
            this.value = value;
        }
    }


}
