package com.czf.json

import com.czf.generateDate
import java.util.*
import kotlin.collections.ArrayList

data class Student(val id: Int, val name: String, val major: String = Companion.major.random(), val birthday: Date = generateDate(21), val gender: String = Companion.gender.random()) {
    companion object {
        private val surnames = arrayOf("赵", "钱", "孙", "李", "周", "吴", "郑", "王", "冯", "陈", "褚", "卫", "蒋", "沈", "韩", "杨", "朱", "秦", "尤", "许",
                "何", "吕", "施", "张", "孔", "曹", "严", "华", "金", "魏", "陶", "姜", "戚", "谢", "邹", "喻", "柏", "水", "窦", "章", "云", "苏", "潘", "葛", "奚", "范", "彭", "郎",
                "鲁", "韦", "昌", "马", "苗", "凤", "花", "方", "俞", "任", "袁", "柳", "酆", "鲍", "史", "唐", "费", "廉", "岑", "薛", "雷", "贺", "倪", "汤", "滕", "殷",
                "罗", "毕", "郝", "邬", "安", "常", "乐", "于", "时", "傅", "皮", "卞", "齐", "康", "伍", "余", "元", "卜", "顾", "孟", "平", "黄", "和",
                "穆", "萧", "尹", "姚", "邵", "湛", "汪", "祁", "毛", "禹", "狄", "米", "贝", "明", "臧", "计", "伏", "成", "戴", "谈", "宋", "茅", "庞", "熊", "纪", "舒",
                "屈", "项", "祝", "董", "梁", "杜", "阮", "蓝", "闵", "席", "季")

        private val names = arrayOf("碧凡", "夏菡", "曼香", "若烟", "半梦", "雅绿", "冰蓝", "灵槐", "平安", "书翠", "翠风", "香巧", "代云", "友巧", "听寒",
                "梦柏", "醉易", "访旋", "亦玉", "凌萱", "访卉", "怀亦", "笑蓝", "春翠", "靖柏", "书雪", "乐枫", "念薇", "靖雁", "寻春", "恨山", "从寒", "忆香",
                "觅波", "静曼", "凡旋", "新波", "代真", "新蕾", "雁玉", "冷卉", "紫山", "千琴", "恨天", "傲芙", "盼山", "怀蝶", "冰兰", "问旋", "从南", "白易",
                "问筠", "如霜", "半芹", "寒雁", "怜云", "寻文", "谷雪", "乐萱", "涵菡", "海莲", "傲蕾", "青槐", "冬儿", "易梦", "惜雪", "宛海", "之柔", "夏青")

        private val major = arrayOf("计算机科学与技术", "人工智能", "信息安全", "网络工程", "软件工程")

        private val gender = arrayOf("男", "女")

        fun randomGenerate(num: Int):MutableList<Student> {
            val list = ArrayList<Student>()

            for(i in 1..num)
                list.add(Student(i, "${surnames.random()}${names.random()}"))

            return list
        }
    }

    override fun equals(other: Any?): Boolean {
        if(other == null)
            return false

        if(other !is Student)
            return false

        return other.id == this.id
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + major.hashCode()
        result = 31 * result + birthday.hashCode()
        result = 31 * result + gender.hashCode()
        return result
    }
}
