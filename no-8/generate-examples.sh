#!/bin/bash

# Number of key-value pairs to generate
num_pairs=2000

# Output file path
output_file="redis_data.txt"

# Generate random key-value pairs and write them to the output file
for ((i = 1; i <= num_pairs; i++)); do
    # Generate random key and value
    random_key=a$(cat /dev/urandom | LC_ALL=C tr -dc '[:print:]' | tr -dc 'a-zA-Z0-9' | head -c 20)
    random_value=$(cat /dev/urandom | LC_ALL=C tr -dc '[:print:]' | tr -dc 'a-zA-Z0-9'| head -c 400)

    # Write key-value pair to the output file
    echo "echo \"SET $random_key $random_value\" | redis-cli -p 6380" >> "$output_file"
done

echo "Random data generated and saved to $output_file."
chmod u+x $output_file
