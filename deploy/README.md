# Holobot - Deployment

This README documents how to set up the VM to automatically deploy Holobot using Docker.

## One-Time VM Setup

1. Install Docker & Docker Compose

```bash
sudo apt update
sudo apt install -y docker.io docker-compose-plugin
sudo systemctl enable docker
sudo systemctl start docker
```

2. Create Holobot directory structure

```bash
mkdir -p /home/ubuntu/holo/{data,logs}
cd /home/ubuntu/holo
```

3. Copy `docker-compose.yml` from this repository to

```bash
/home/ubuntu/holo/docker-compose.yml
```