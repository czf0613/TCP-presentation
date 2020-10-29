//
//  Models.swift
//  tcp
//
//  Created by Zhifan Chen on 2020/10/13.
//

import Foundation
import HandyJSON
import SwiftDate
import UIKit
import SwiftUI

struct Student: HandyJSON, Hashable {
    var id = 0
    var name = ""
    var gender = ""
    var major = ""
    var birthday = 0
}

struct Message: HandyJSON, Hashable {
    var sender = ""
    var content = ""
    var time = Date().nanosecond / 1000
}

struct AlertData: Identifiable {
    var id = UUID()
    var title = ""
    var content = ""
}

struct SheetData: Identifiable {
    var id = UUID()
    var statusCode = 404
    var content:String?
}

extension Int {
    func toTimeString() -> String {
        let date = Date(milliseconds: self)
        return date.toFormat("yyyy-MM-dd")
    }
}

extension String {
    func asValidUrl() -> String {
        return self.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? "https://moral-helper.online:8248/studens"
    }
}

extension UITableView {
    func scrollToBottom(animated: Bool, yOffset: Binding<CGFloat>) {
        let y = contentSize.height - frame.size.height
        if (y < 0) {
            return
        }
        yOffset.wrappedValue = y
    }
}
