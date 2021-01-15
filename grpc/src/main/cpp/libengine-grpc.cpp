/*
 * This file is part of Adblock Plus <https://adblockplus.org/>,
 * Copyright (C) 2006-present eyeo GmbH
 *
 * Adblock Plus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation.
 *
 * Adblock Plus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Adblock Plus.  If not, see <http://www.gnu.org/licenses/>.
 */

#include <jni.h>
#include <string>
#include <vector>
#include <matches.pb.h>
#include <matches.grpc.pb.h>
#include <grpc++/grpc++.h>

using grpc::Channel;
using grpc::ClientContext;
using grpc::Server;
using grpc::ServerBuilder;
using grpc::ServerContext;
using grpc::Status;

using namespace com::eyeo::ctu::engine::grpc;

// Logic and data behind the server's behavior.
class EngineServiceImpl final : public EngineService::Service
{
    Status matches(ServerContext* context,
                   const MatchesRequest* request,
                   MatchesResponse* response) override
   {
        // server logic: just for the test
        response->mutable_filter()->set_pointer(request->url().length());
        return Status::OK;
    }
};

std::unique_ptr<Server> server;
EngineServiceImpl service;

std::string JniJavaToStdString(JNIEnv* env, jstring str)
{
    if (!str)
    {
        return std::string();
    }

    const char* cStr = env->GetStringUTFChars(str, 0);
    std::string ret(cStr);
    env->ReleaseStringUTFChars(str, cStr);

    return ret;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_eyeo_ctu_engine_grpc_Rpc_start(JNIEnv *env, jobject thiz, jstring jAddress)
{
    auto addr_uri = JniJavaToStdString(env, jAddress);

    ServerBuilder builder;
    // Listen on the given address without any authentication mechanism.
    builder.AddListeningPort(addr_uri, grpc::InsecureServerCredentials());
    // Register "service" as the instance through which we'll communicate with
    // clients. In this case it corresponds to an *synchronous* service.
    builder.RegisterService(&service);
    // Finally assemble the server.
    server = std::move(builder.BuildAndStart());
}

extern "C"
JNIEXPORT void JNICALL
Java_com_eyeo_ctu_engine_grpc_Rpc_shutdownNow(JNIEnv *env, jobject thiz)
{
    if (!server)
        return;

    server->Shutdown();
    server->Wait();
    server.reset();
}