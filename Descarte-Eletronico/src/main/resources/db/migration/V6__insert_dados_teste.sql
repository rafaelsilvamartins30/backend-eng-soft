-- 1. Inserir Categorias (TipoProduto)
INSERT INTO tipo_produto (id, version, created_at, updated_at, entity_status, nome, descricao_exemplos) VALUES
                                                                                                            ('8b1e529d-ab77-4f1e-b77c-e0516b66b640', 0, NOW(), NOW(), 'ACTIVE', 'Eletrônicos', 'Celulares, tablets, monitores'),
                                                                                                            ('06636b21-f383-4865-8d68-280b39ba8794', 0, NOW(), NOW(), 'ACTIVE', 'Pilhas e Baterias', 'Pilhas AAA, AA, baterias de lítio'),
                                                                                                            ('92eb9203-db46-48cf-82e3-5a6c194eb636', 0, NOW(), NOW(), 'ACTIVE', 'Eletrodomésticos', 'Liquidificadores, batedeiras, micro-ondas'),
                                                                                                            ('b1f18af5-0a67-423b-9705-caa8a8db0c24', 0, NOW(), NOW(), 'ACTIVE', 'Informática', 'Mouses, teclados, cabos, placas-mãe');


-- 2. Inserir Pontos de Coleta (Lavras/MG)
INSERT INTO ponto_coleta (id, version, created_at, updated_at, entity_status, nome, endereco, descricao, latitude, longitude, horario_abertura, horario_fechamento) VALUES
-- Cantina Central UFLA
('da6fa403-0f3a-4169-9dce-31dfe7a954cc', 0, NOW(), NOW(), 'ACTIVE', 'Cantina Central da UFLA', 'Avenida Central UFLA, s/n', 'Ponto de coleta localizado em frente à livraria da UFLA.', -21.227212, -44.977778, '07:00', '19:00'),

-- Secretaria do DCC
('52e1b77d-b5c3-4577-a587-c7a517ebe864', 0, NOW(), NOW(), 'ACTIVE', 'Secretaria do DCC (UFLA)', 'Avenida Central UFLA, s/n - DCC', 'Coletor focado em peças de computador e periféricos.', -21.227757, -44.978522, '08:00', '18:00'),

-- Praça Dr. Augusto Silva
('cd187c6a-dfc6-4beb-ad9f-af1f49937e7f', 0, NOW(), NOW(), 'ACTIVE', 'Ecoponto Praça Augusto Silva', 'Praça Dr. Augusto Silva - Centro, Lavras - MG, 37200-000', 'Ponto de descarte geral no centro da cidade.', -21.241496, -44.998615, '00:00', '23:59'),

-- Supermercado Rex Shopping
('5bc2e869-40bb-4883-b8b0-f747d49e5576', 0, NOW(), NOW(), 'ACTIVE', 'Coletor Supermercado Rex', 'R. Dr. Antônio Gonçalves de Faria, 150 - Sra. do Líbano, Lavras - MG, 37200-000', 'Caixa coletora logo na entrada principal do supermercado.', -21.248188, -44.989774, '08:00', '22:00'),

-- Shopping Cidade da Serra
('f1f3809e-c218-41cf-a2c4-6fe551ebcddd', 0, NOW(), NOW(), 'ACTIVE', 'Shopping Cidade da Serra', 'Av. 18 de Maio, 89 - Chácara São Geraldo, Lavras - MG, 37206-840', 'Coleta segura localizada no corredor de serviços do shopping.', -21.265657, -45.001074, '10:00', '22:00');


-- 3. Associar Pontos de Coleta aos Tipos de Produto (Relacionamento N:N)
INSERT INTO ponto_coleta_tipo_produto (ponto_coleta_id, tipo_produto_id) VALUES
-- Cantina Central UFLA coleta Pilhas
('da6fa403-0f3a-4169-9dce-31dfe7a954cc', '06636b21-f383-4865-8d68-280b39ba8794'),

-- Secretaria do DCC coleta Informática e Eletrônicos
('52e1b77d-b5c3-4577-a587-c7a517ebe864', 'b1f18af5-0a67-423b-9705-caa8a8db0c24'),
('52e1b77d-b5c3-4577-a587-c7a517ebe864', '8b1e529d-ab77-4f1e-b77c-e0516b66b640'),

-- Praça coleta todos os 4 tipos
('cd187c6a-dfc6-4beb-ad9f-af1f49937e7f', '8b1e529d-ab77-4f1e-b77c-e0516b66b640'),
('cd187c6a-dfc6-4beb-ad9f-af1f49937e7f', '06636b21-f383-4865-8d68-280b39ba8794'),
('cd187c6a-dfc6-4beb-ad9f-af1f49937e7f', '92eb9203-db46-48cf-82e3-5a6c194eb636'),
('cd187c6a-dfc6-4beb-ad9f-af1f49937e7f', 'b1f18af5-0a67-423b-9705-caa8a8db0c24'),

-- Rex coleta Pilhas e Eletrodomésticos
('5bc2e869-40bb-4883-b8b0-f747d49e5576', '06636b21-f383-4865-8d68-280b39ba8794'),
('5bc2e869-40bb-4883-b8b0-f747d49e5576', '92eb9203-db46-48cf-82e3-5a6c194eb636'),

-- Shopping coleta Eletrônicos e Informática
('f1f3809e-c218-41cf-a2c4-6fe551ebcddd', '8b1e529d-ab77-4f1e-b77c-e0516b66b640'),
('f1f3809e-c218-41cf-a2c4-6fe551ebcddd', 'b1f18af5-0a67-423b-9705-caa8a8db0c24');