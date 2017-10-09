package com.alebit.sshd;

import org.apache.sshd.common.forward.DefaultTcpipForwarderFactory;
import org.apache.sshd.common.session.Session;
import org.apache.sshd.common.util.net.SshdSocketAddress;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator;
import org.apache.sshd.server.forward.ForwardingFilter;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.scp.ScpCommandFactory;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.shell.ProcessShellFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.PublicKey;

public class Main {
    public Main() {
        SshServer sshd = SshServer.setUpDefaultServer();
        sshd.setPort(8080);
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(Paths.get("hostkey.ser")));
        sshd.setShellFactory(new ProcessShellFactory(new String[] { "/bin/sh", "-i", "-l" }));
        sshd.setCommandFactory(new ScpCommandFactory());
        sshd.setTcpipForwardingFilter(new ForwardingFilter() {
            @Override
            public boolean canForwardAgent(Session session, String s) {
                return true;
            }

            @Override
            public boolean canForwardX11(Session session, String s) {
                return true;
            }

            @Override
            public boolean canListen(SshdSocketAddress sshdSocketAddress, Session session) {
                return true;
            }

            @Override
            public boolean canConnect(Type type, SshdSocketAddress sshdSocketAddress, Session session) {
                return true;
            }
        });
        sshd.setTcpipForwarderFactory(new DefaultTcpipForwarderFactory());
        sshd.setPublickeyAuthenticator(new PublickeyAuthenticator() {
            @Override
            public boolean authenticate(String s, PublicKey publicKey, ServerSession serverSession) {
                return true;
            }
        });
        try {
            sshd.start();
            while (true) {
                Thread.sleep(Integer.MAX_VALUE);
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Main();
    }
}
