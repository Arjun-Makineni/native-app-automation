set_pipeline:
	fly -t test-automation set-pipeline -c main.yml -p native-apps-automation
	fly -t test-automation expose-pipeline --pipeline native-apps-automation
default: set_pipeline ;