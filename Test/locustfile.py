from locust import HttpUser, task, between

class InventoryUser(HttpUser):
    wait_time = between(0.1, 0.5)

    @task
    def purchase_flow(self):
        product_id = "101"

        with self.client.post(
                f"/api/inventory/buy/{product_id}",
                catch_response=True
        ) as response:

            if response.status_code != 200:
                response.failure(f"Failed with status {response.status_code}")

            elif "Sold Out" in response.text:
                response.failure("Out of Stock!")