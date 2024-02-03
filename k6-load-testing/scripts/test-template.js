import {authenticate} from "./authentication_template.js";
import http from "k6/http";
import { check, sleep } from "k6";

const REQUEST_TIMEOUT = "180s"; // 3 minutes
const REQUEST_SLEEP = 0.5; // seconds

/**
 * Test case configuration
 */
export const options = {
    tags: {
        test: 'load-test-template',
        test_run_id: `Load-Test-Performance-${__ENV.K6_ENV}`,
    },
    thresholds: {
        // http thresholds
        'http_req_failed{test_type:uc1}': ['rate<0.01'], // http errors should be less than 1% > System availability
        'http_req_duration{test_type:uc1}': ['p(95)<400'], // 95% of requests should be below 400ms > System latency
    },
    scenarios: {
        // Load testing using K6 Ramping-rate scenario
        uc1: {
            executor: 'ramping-arrival-rate',
            startTime: '0s', // the ramping API test starts a little later
            startRate: 10,  // we start at 10 iterations per second (timeUnit)
            timeUnit: '1s',
            stages: [
                {target: 20, duration: '30s'}, // go from 10 to 20 iters/s in the first 30 seconds
                {target: 20, duration: '1m'}, // hold at 20 iters/s for 1 minute
                {target: 0, duration: '30s'}, // ramp down back to 0 iters/s over the last 30 second
            ],
            preAllocatedVUs: 4, // the size of the VU (i.e. worker) pool for this scenario > represents crs and retail-bank instances
            maxVUs: 10, // if the preAllocatedVUs are not enough, we can initialize more
            tags: {test_type: 'uc1'}, // different extra metric tags for this scenario
            exec: 'uc1', // same function as the scenario above, but with different env vars
        }
    }
};

//----------------------------------------------------------------------------------------------------
/**
 * prepare the test data like authentication
 * @returns Initial data for each test case
 */
export function setup() {
    const authBody = authenticate();
    return { access_token: authBody.access_token };
}
//----------------------------------------------------------------------------------------------------

/**
 * uc1
 */
export function uc1(data) {

    const url = __ENV.SERVICE_URL;
    const headers = {
        'Authorization': `Bearer ${data.access_token}`,
        'Content-Type': 'application/json',
        'version': '1.0.0'
    };

    const payload = `{
    "payload":"sample"
  }`;

    var response = http.post(url, payload, { timeout: REQUEST_TIMEOUT, headers: headers });

    check(response, { 'status is 200': (r) => r.status === 200 });
    if(response.status !== 200) {
        console.log(`operation: uc1, url: ${url}, Status:${response.status}`);
    }

    sleep(REQUEST_SLEEP);
}
//----------------------------------------------------------------------------------------------------