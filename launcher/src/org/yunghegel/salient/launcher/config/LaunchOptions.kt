package org.yunghegel.salient.launcher.config

class LaunchOptions(val args: Array<String>) {

    fun whenOption(option: String, action: (String) -> Unit) {
        val index = args.indexOf(option)
        if (index != -1) {
            action(args[index + 1])
        }
    }

    fun optionWithValue(option: String): String? {
        val index = args.indexOf(option)
        return if (index != -1) {
            args[index + 1]
        } else {
            null
        }
    }

    fun option(name:String, action: OptionBuilder.()->Unit) {
        var contains = args.contains(name)

        val b = OptionBuilder()
        b.action()
        val handler = b.build()

        if (!contains) handler.otherwise(name)
        else {
            val split = name.split("=")
            val (key,value ) = Pair(split[0],split[1])
            handler.present(key,value)
        }

    }

    class OptionBuilder {
        var ifPresent : (String,String?) -> Unit = {_,_ ->}
        var ifNot : (String) -> Unit = { _ ->}
        internal fun build() : OptionHandler {
            return object : OptionHandler {
                override fun otherwise(name: String) {
                    ifNot(name)
                }
                override fun present(name: String, value: String?) {
                    ifPresent(name,value)
                }
            }
        }
    }

    interface OptionHandler {

        fun present(name:String, value: String? = null)

        fun otherwise(name:String)

    }



}