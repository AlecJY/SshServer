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
    public static int port = 8080;

    public Main() {
        SshServer sshd = SshServer.setUpDefaultServer();
        sshd.setPort(port);
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(Paths.get("hostkey.ser")));
        sshd.setShellFactory(new ProcessShellFactory(new String[] { "/bin/bash" }));
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
            System.out.println("Starting SSH Server at port " + port);
            sshd.start();
            System.out.println("Started SSH Server successfully!");
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
