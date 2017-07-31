package com.bettercloud.vault.response;

import com.bettercloud.vault.json.Json;
import com.bettercloud.vault.json.JsonObject;
import com.bettercloud.vault.json.JsonValue;
import com.bettercloud.vault.json.ParseException;
import com.bettercloud.vault.rest.RestResponse;

import java.io.UnsupportedEncodingException;

/**
 * This class is a container for the information returned by Vault in system API
 * operations (e.g. policy).
 */
public class SysResponse extends LogicalResponse {

    private JsonObject rules;
    private String name;

    public SysResponse(final RestResponse restResponse, final int retries) {
        super(restResponse, retries);

        try {
            final String responseJson = new String(restResponse.getBody(), "UTF-8");
            final JsonObject jsonObject = Json.parse(responseJson).asObject();
            rules = getRules(jsonObject);
            name = jsonObject.getString("name", "");

        } catch (UnsupportedEncodingException | ParseException e) {
        }
    }

    public JsonObject getRules() { return rules; }

    public String getName() { return name; }

    private JsonObject getRules(JsonObject response) {
        JsonValue rulesValue = response.get("rules");
        return shouldParseRules(rulesValue) ? Json.parse(rulesValue.asString()).asObject() : new JsonObject();
    }

    private boolean shouldParseRules(JsonValue rulesValue) {
        return null != rulesValue && rulesValue.isString() && !rulesValue.asString().equals("");
    }
}
