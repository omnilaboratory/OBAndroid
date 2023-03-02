package com.omni.wallet.client;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLException;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import toolrpc.luckPkApiGrpc;

/**
 * 汉: LuckPk客户端
 * En: LuckPkClient
 * author: guoyalei
 * date: 2023/2/27
 */
public class LuckPkClient {
    public ManagedChannel channel;
    public luckPkApiGrpc.luckPkApiBlockingStub blockingStub;

    /**
     * Construct client connecting to gRPC server at {@code host:port}.
     *
     * @throws SSLException
     */
    public LuckPkClient(String host, int port, String certFile, String certKey) throws SSLException {
        this(NettyChannelBuilder.forAddress(host, port)
                .negotiationType(NegotiationType.TLS)
                .sslContext(buildSslContext(certFile, certKey)));
    }

    private static SslContext buildSslContext(String clientCertFilePath,
                                              String clientKeyFilePath) throws SSLException {
        SslContextBuilder builder = GrpcSslContexts.forClient();
        builder.trustManager(InsecureTrustManagerFactory.INSTANCE);
        if (clientCertFilePath != null && clientKeyFilePath != null) {
            builder.keyManager(new File(clientCertFilePath), new File(clientKeyFilePath));
        }
        return builder.build();
    }

    /**
     * Construct client for accessing RouteGuide server using the existing channel.
     */
    public LuckPkClient(ManagedChannelBuilder<?> channelBuilder) {
        channel = channelBuilder.build();
        blockingStub = luckPkApiGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}