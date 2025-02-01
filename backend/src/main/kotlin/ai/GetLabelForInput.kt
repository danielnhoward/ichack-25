package com.example.ai

private val prompt = """
    You are tasked with automatically generating a label for an HTML input field based on the provided attributes. Your goal is to create a clear, concise, and user-friendly label that accurately describes the purpose of the input field.

    You will be given the following attributes of an HTML input field:

    <id>{{ID}}</id>
    <name>{{NAME}}</name>
    <type>{{TYPE}}</type>
    <value>{{VALUE}}</value>

    To generate an appropriate label, follow these steps:

    1. Analyze the provided attributes, focusing primarily on the 'name' and 'id' fields.
    2. Convert any camelCase or snake_case formatting to regular text with proper spacing.
    3. Capitalize the first letter of each word, except for articles and prepositions.
    4. If the 'type' attribute provides additional context, consider incorporating it into the label.
    5. Keep the label concise, ideally no more than 3-5 words.
    6. Ensure the label is clear and descriptive for the average user.

    Format your output as follows:
    <label>Your generated label here</label>

    Here are some examples:

    Input:
    <id>userEmail</id>
    <name>user_email</name>
    <type>email</type>
    Output:
    <label>Email Address</label>

    Input:
    <id>dob</id>
    <name>dateOfBirth</name>
    <type>date</type>
    Output:
    <label>Date of Birth</label>

    Now, generate a label for the provided HTML input field attributes. Present your final output without any html tags
""".trimIndent()

suspend fun getLabelForInput(
    id: String,
    name: String,
    type: String,
    value: String): String {
    val req = AnthropicRequest(listOf(Message(content=listOf(
        TextContent(prompt.replace("{{ID}}", id).replace("{{NAME}}", name).replace("{{TYPE}}", type).replace("{{VALUE}}", value))
    ))))
    val res = AnthropicAPI.request(req)
    res.content.first { it is TextContent }.let {
        return (it as TextContent).text
    }
}

