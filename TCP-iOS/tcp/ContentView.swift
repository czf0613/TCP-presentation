//
//  ContentView.swift
//  tcp
//
//  Created by Zhifan Chen on 2020/10/13.
//

import SwiftUI
import Alamofire
import Starscream
import UIKit
import Introspect

func endEditing() {
    UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
}

struct ContentView: View {
    let http = Http()
    let ws = WS()
    @State var page = "Http请求"
    
    var body: some View {
        NavigationView {
            TabView(selection: $page) {
                http.tabItem {
                    Image(systemName: "network")
                    Text("HTTP")
                }.tag("Http请求")
                
                ws.tabItem {
                    Image(systemName: "personalhotspot")
                    Text("WebSocket")
                }.tag("网络聊天室")
            }.navigationBarTitle(page, displayMode: .inline)
        }
    }
}

struct Http: View {
    @State var students:[Student] = []
    @State var getId = ""
    @State var postId = ""
    @State var postName = ""
    @State var deleteId = ""
    @State var sheetData:SheetData?
    @State var alertData:AlertData?
    
    var body: some View {
        VStack {
            List {
                if(students.isEmpty) {
                    Text("没有信息").padding()
                } else {
                    ForEach(students, id: \.self) {
                        StudentItem(data: $0).padding()
                    }
                }
            }.frame(height: 250)
            
            Divider().frame(height: 40)
            
            ScrollView {
                Text("测试GET方法").font(.system(size: 25)).foregroundColor(.blue)
                
                HStack {
                    TextField("请输入学生id", text: $getId).lineLimit(1).ignoresSafeArea(.keyboard).keyboardType(.numberPad)
                    Button(action: {
                        get()
                    }, label: {
                        Label("GET!", systemImage: "square.and.arrow.down")
                    })
                }.padding()
                
                Text("测试POST方法").font(.system(size: 25)).foregroundColor(.orange)
                
                HStack {
                    VStack {
                        TextField("请输入学生id", text: $postId).padding([.top, .bottom], /*@START_MENU_TOKEN@*/10/*@END_MENU_TOKEN@*/).lineLimit(1).ignoresSafeArea(.keyboard).keyboardType(.numberPad)
                        TextField("请输入学生姓名", text: $postName).padding([.top, .bottom], 10).lineLimit(1).ignoresSafeArea(.keyboard)
                    }
                    
                    Button(action: {
                        post()
                    }, label: {
                        Label("POST!", systemImage: "paperplane")
                    })
                }.padding()
                
                Text("测试DELETE方法").font(.system(size: 25)).foregroundColor(.red)
                
                HStack {
                    TextField("请输入学生id", text: $deleteId).lineLimit(1).ignoresSafeArea(.keyboard).keyboardType(.numberPad)
                    Button(action: {
                        delete()
                    }, label: {
                        Label("DELETE!", systemImage: "trash")
                    })
                }.padding()
            }
        }.sheet(item: $sheetData) {data in
            SheetItem(sheetData: data)
        }.onAppear {
            getAll()
        }.alert(item: $alertData) {
            Alert(title: Text($0.title), message: Text($0.content), dismissButton: .cancel())
        }.onTapGesture {
            endEditing()
        }
    }
    
    func getAll() {
        AF.request("https://moral-helper.online:8248/students", method: .get).responseString { data in
            switch data.result {
                case .success(let string):
                    if(data.response!.statusCode == 200) {
                        students = ([Student].deserialize(from: string) ?? []).map { $0! }
                    }
                case .failure(_): break
            }
        }
    }
    
    func get() {
        if(getId.isEmpty) {
            alertData = AlertData(title: "错误", content: "请输入学生id")
            return
        }
        
        AF.request("https://moral-helper.online:8248/students/\(getId)".asValidUrl(), method: .get).responseString { data in
            sheetData = SheetData(statusCode: data.response?.statusCode ?? 404, content: try? data.result.get())
        }
    }
    
    func post() {
        if(postId.isEmpty) {
            alertData = AlertData(title: "错误", content: "请输入学生id")
            return
        }
        
        if(postName.isEmpty) {
            alertData = AlertData(title: "错误", content: "请输入学生姓名")
            return
        }
        
        AF.request("https://moral-helper.online:8248/students/new", method: .post, parameters: ["id":postId, "name":postName]).responseString { data in
            sheetData = SheetData(statusCode: data.response?.statusCode ?? 404, content: try? data.result.get())
            getAll()
        }
    }
    
    func delete() {
        if(deleteId.isEmpty) {
            alertData = AlertData(title: "错误", content: "请输入学生id")
            return
        }
        
        AF.request("https://moral-helper.online:8248/students/delete/\(deleteId)".asValidUrl(), method: .delete).responseString { data in
            sheetData = SheetData(statusCode: data.response?.statusCode ?? 404, content: try? data.result.get())
            getAll()
        }
    }
}

struct StudentItem: View {
    let data:Student
    
    var body: some View {
        HStack {
            Image(systemName: "person.crop.circle").frame(width: 40, height: 40).padding()
            
            VStack {
                Text("学生id：\(data.id)").font(.system(size: 20)).lineLimit(1).padding(5).foregroundColor(.orange)
                
                Text("学生姓名：\(data.name)").font(.system(size: 20)).lineLimit(1).padding(5)
                
                Text("性别：\(data.gender)").font(.system(size: 20)).lineLimit(1).padding(5)
                
                Text("专业：\(data.major)").font(.system(size: 20)).lineLimit(1).padding(5)
                
                Text("出生日期：\(data.birthday.toTimeString())").font(.system(size: 20)).lineLimit(1).padding(5)
            }
        }
    }
}

struct SheetItem: View {
    let sheetData:SheetData
    
    var body: some View {
        VStack {
            Text("状态码：\(sheetData.statusCode)").font(.system(size: 25)).padding().foregroundColor(sheetData.statusCode >= 400 ? .red : .green)
            
            Text(sheetData.content ?? "No Response").font(.system(size: 20)).lineSpacing(5.0).lineLimit(Int.max).padding().foregroundColor(.yellow)
            
            Spacer()
        }
    }
}

struct WS: View {
    @State var user = ""
    @State var hidden = false
    @State var toSend = ""
    @State var messages:[MessageView] = []
    @State var websocket:WebSocket?
    @State var yOffset: CGFloat = 0
    @State var tableView: UITableView?
    
    var body: some View {
        VStack {
            List {
                Text(Date().toFormat("HH:mm zzz"))
                
                ForEach(0..<messages.count, id:\.self) {
                    messages[$0]
                }
            }.introspectTableView(customize: { tableView in
                if (self.tableView == nil) {
                    self.tableView = tableView
                } else {
                    self.tableView?.setContentOffset(CGPoint(x: 0, y: self.yOffset + 60), animated: true)
                }
            })
            
            if(!hidden) {
                HStack {
                    TextField("请输入昵称", text: $user).padding().ignoresSafeArea(.keyboard)
                    Button(action: {
                        if(!user.isEmpty) {
                            startWS()
                        }
                    }, label: {
                        Label("加入群聊", systemImage: "person.fill.badge.plus")
                    })
                }.padding()
            }
            
            HStack {
                TextField("跟大家说点啥？", text: $toSend).padding().ignoresSafeArea(.keyboard).keyboardType(.twitter)
                Button(action: {
                    if(!toSend.isEmpty) {
                        send(content: toSend)
                        toSend = ""
                    }
                }, label: {
                    Label("发送", systemImage: "paperplane")
                })
            }.padding()
        }.onTapGesture {
            endEditing()
        }
    }
    
    func startWS() {
        var url = URLRequest(url: try! "wss://moral-helper.online:8248/message/\(user)".asValidUrl().asURL())
        url.timeoutInterval = 5
        websocket = WebSocket(request: url)
        websocket?.onEvent = { event in
            switch event {
                case .connected(_):
                    hidden = true
                case .cancelled:
                    hidden = false
                case .text(let string):
                    let message = Message.deserialize(from: string)
                    if(message == nil) {
                        messages.append(MessageView(isRaw: true, content: string))
                    } else {
                        messages.append(MessageView(isRaw: false, isSelf: message?.sender == user , message: message))
                    }
                    tableView?.scrollToBottom(animated: true, yOffset: $yOffset)
                case .error(_):
                    hidden = false
                default: break
            }
        }
        websocket?.connect()
    }
    
    func send(content:String) {
        let message = Message(sender: user, content: content)
        websocket?.write(string: message.toJSONString() ?? "{}")
        messages.append(MessageView(isRaw: false, isSelf: true, message: message))
        tableView?.scrollToBottom(animated: true, yOffset: $yOffset)
    }
}

struct MessageView: View {
    let isRaw:Bool
    var isSelf = false
    var content:String? = nil
    var message:Message? = nil
    
    var body: some View {
        HStack {
            if(isRaw) {
                Text(content!).padding()
            } else {
                if(isSelf) {
                    Text(message!.content).lineLimit(Int.max).frame(maxWidth: 10000)
                    Image(systemName: "person").frame(width: 30, height: 30).padding()
                } else {
                    VStack {
                        Image(systemName: "person").frame(width: 30, height: 30).padding()
                        
                        Text(message!.sender).lineLimit(1)
                    }
                    
                    Text(message!.content).lineLimit(Int.max)
                }
            }
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
