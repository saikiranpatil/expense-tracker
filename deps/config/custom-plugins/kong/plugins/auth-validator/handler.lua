-- Make sure this file is at: plugins/kong/plugins/auth-validator/handler.lua
local http = require "resty.http"
local cjson = require "cjson.safe"

local AuthValidator = {
  PRIORITY = 1000,
  VERSION = "1.0.0"
}

function AuthValidator:access(conf)
  local client = http.new()

  -- Store the original request details
  local original_uri = kong.request.get_path()
  local original_method = kong.request.get_method()
  local original_headers = kong.request.get_headers()

  -- Prepare headers for the auth service request
  local headers = {
    ["Content-Type"] = "application/json"
  }

  -- Forward all authentication-related headers
  if original_headers["Authorization"] then
    headers["Authorization"] = original_headers["Authorization"]
  end

  -- Forward other potential auth headers
  if original_headers["x-api-key"] then
    headers["X-API-Key"] = original_headers["x-api-key"]
  end

  if original_headers["x-access-token"] then
    headers["X-Access-Token"] = original_headers["x-access-token"]
  end

  -- Add any additional custom auth headers you need
  -- For example: headers["X-Custom-Auth"] = original_headers["x-custom-auth"]

  -- Make request to auth service
  local res, err = client:request_uri(conf.auth_url, {
    method = "GET",
    headers = headers
  })

  if res then
    kong.log.err("Res: ", res)
  end
  kong.log.err("Auth: ", headers["Authorization"])
  kong.log.err("Url: ", conf.auth_url)
  kong.log.err("Error: ", err)

  -- Check if the auth request was successful
  --if not res then
  --  kong.log.err("Failed to reach auth service: ", err)
  --  return kong.response.exit(500, { error = "Internal server error" })
  --end

  -- Handle authentication failure
  --if res.status >= 400 then
  --  kong.log.err("Authentication failed with status: ", res.status)
  --  return kong.response.exit(401, { message = "Unauthorized" })
  --end

  -- Parse the response to get the user ID
  local body, err = cjson.decode(res.body)

  --if not body or not body.userId then
  --  kong.log.err("Invalid response from auth service: missing userId")
  --  return kong.response.exit(401, { message = "Unauthorized" })
  --end

  -- Add the user ID to the request headers for downstream services
  kong.service.request.set_header("X-User-Id", body.userId)

  -- Preserve the original authentication headers for downstream services
  if original_headers["Authorization"] then
    kong.service.request.set_header("Authorization", original_headers["Authorization"])
  end

  -- Allow the request to continue to its original destination
  --kong.log.debug("Request authenticated for user: ", body.userId)
end

return AuthValidator