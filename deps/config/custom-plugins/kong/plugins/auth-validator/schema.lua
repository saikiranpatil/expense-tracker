-- Make sure this file is at: plugins/kong/plugins/auth-validator/schema.lua
return {
  name = "auth-validator",
  fields = {
    { config = {
        type = "record",
        fields = {
          { auth_url = { type = "string", required = true, default = "http://auth-service:9898/auth/ping" } }
        }
      }
    }
  }
}