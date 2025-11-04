package com.resq.app.data.util

/**
 * NOTE: To switch to a production environment, the baseUrl in the network client
 * builder should be changed from the development placeholder.
 */
object ServoFixture {

    const val statusResponse = """
    {
      "timestamp":"2025-10-27T12:34:56Z",
      "servos":{"s1":90}
    }
    """

    const val setRequest = """
    {
      "id":"s1",
      "position":120
    }
    """

    const val setResponse = """
    {
      "status":"OK",
      "id":"s1",
      "position":120,
      "message":"queued"
    }
    """

    const val socketUpdate = """
    {
      "id":"s1",
      "position":120,
      "status":"OK",
      "timestamp":"2025-10-27T12:34:57Z"
    }
    """
}
