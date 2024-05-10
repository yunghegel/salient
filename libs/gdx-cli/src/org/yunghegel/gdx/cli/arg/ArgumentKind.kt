package org.yunghegel.gdx.cli.arg

enum class ArgumentKind {
    /*
    * A flag is an argument that does not have a value.
    * Format: -f, --flag
    */
    FLAG,
    /*
     * A positional argument is a key-value pair.
     * Format: key=value key value
     */
    ARGUMENT,
    /*
     * An option is an optional key-value pair with a flag key.
     * Format: --key=value --key value
     */
    OPTION
}