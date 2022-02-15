package com.digital.opuserp.view.dashboard;


import java.util.Iterator;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.SubModuloDAO;
import com.digital.opuserp.view.home.PaginaInicialAdminErrorView;
import com.digital.opuserp.view.home.PaginaInicialView;
import com.digital.opuserp.view.modulos.Help;
import com.digital.opuserp.view.modulos.acesso.base.BaseView;
import com.digital.opuserp.view.modulos.acesso.caixa_atendimento.CaixaDeAtendimentoView;
import com.digital.opuserp.view.modulos.acesso.central_assinantes.CentralAssinantesView;
import com.digital.opuserp.view.modulos.acesso.concentrador.ConcentradorView;
import com.digital.opuserp.view.modulos.acesso.contratos_acesso.ContratoAcessoView;
import com.digital.opuserp.view.modulos.acesso.olts.OltView;
import com.digital.opuserp.view.modulos.acesso.planos.PlanosView;
import com.digital.opuserp.view.modulos.cadastro.ceps.CepView;
import com.digital.opuserp.view.modulos.cadastro.clientes.ClientesView;
import com.digital.opuserp.view.modulos.cadastro.fornecedores.FornecedorView;
import com.digital.opuserp.view.modulos.cadastro.setores.SetoresView;
import com.digital.opuserp.view.modulos.cadastro.transportadoras.TransportadoraView;
import com.digital.opuserp.view.modulos.cadastro.veiculos.VeiculosView;
import com.digital.opuserp.view.modulos.configuracoes.config_acesso.TabSheetAcessoView;
import com.digital.opuserp.view.modulos.configuracoes.config_acesso.contratos.ContratosView;
import com.digital.opuserp.view.modulos.configuracoes.config_acesso.materiais.ConfigMaterialAcessoView;
import com.digital.opuserp.view.modulos.configuracoes.config_cadastro.TabSheetConfigCadastroView;
import com.digital.opuserp.view.modulos.configuracoes.config_cobranca_email.ConfigCobrancaEmailView;
import com.digital.opuserp.view.modulos.configuracoes.config_crm.ConfigCRMView;
import com.digital.opuserp.view.modulos.configuracoes.config_estoque.ConfigEstoqueView;
import com.digital.opuserp.view.modulos.configuracoes.config_financeiro.ConfigFinanceiroView;
import com.digital.opuserp.view.modulos.configuracoes.config_financeiro.contrato_cobranca.ContratoCobrancaView;
import com.digital.opuserp.view.modulos.configuracoes.config_nfe.ConfigNfeView;
import com.digital.opuserp.view.modulos.configuracoes.config_ordemServico.ConfigOrdemServicoView;
import com.digital.opuserp.view.modulos.configuracoes.config_sistema.TabSheetConfigSistemaView;
import com.digital.opuserp.view.modulos.configuracoes.config_sistema.usuario.UsuarioView;
import com.digital.opuserp.view.modulos.configuracoes.log.LogView;
import com.digital.opuserp.view.modulos.contratoManutencao.ContratoManutencaoView;
import com.digital.opuserp.view.modulos.contratoManutencao.PlanoManutencaoView;
import com.digital.opuserp.view.modulos.crm.crm.ContatoView;
import com.digital.opuserp.view.modulos.estoque.ficha_cega.FichaCegaView;
import com.digital.opuserp.view.modulos.estoque.gerenciamento.GerenciamentoEstoqueView;
import com.digital.opuserp.view.modulos.estoque.grupo.GrupoView;
import com.digital.opuserp.view.modulos.estoque.marca.MarcaView;
import com.digital.opuserp.view.modulos.estoque.produto.ProdutoView;
import com.digital.opuserp.view.modulos.estoque.serial.SerialView;
import com.digital.opuserp.view.modulos.estoque.tabelaPreco.TabelaPrecoView;
import com.digital.opuserp.view.modulos.estoque.tipoItem.TipoItemView;
import com.digital.opuserp.view.modulos.financeiro.contasPagar.ContasPagarView;
import com.digital.opuserp.view.modulos.financeiro.contasReceber.BaixaRapidaView;
import com.digital.opuserp.view.modulos.financeiro.contasReceber.ContasReceberView;
import com.digital.opuserp.view.modulos.financeiro.formasPgto.FormasPgtoView;
import com.digital.opuserp.view.modulos.financeiro.haver.HaverView;
import com.digital.opuserp.view.modulos.financeiro.preferencias.PreferenciaView;
import com.digital.opuserp.view.modulos.nfe.modelo_21.arquivos_remessa.ArquivosRemessaView;
import com.digital.opuserp.view.modulos.nfe.modelo_21.notas.NotasView;
import com.digital.opuserp.view.modulos.ordemServico.assistenciaTecnica.AssistenciaTecnicaView;
import com.digital.opuserp.view.modulos.ordemServico.materiaisAlocados.MateriaisAlocadosView;
import com.digital.opuserp.view.modulos.ordemServico.ordemProducao.OrdemProducaoView;
import com.digital.opuserp.view.modulos.ordemServico.pedidoServico.PedidoServicoView;
import com.digital.opuserp.view.modulos.ordemServico.roteirizacao.RoteirizacaoView;
import com.digital.opuserp.view.modulos.ordemServico.servicos.ServicoView;
import com.digital.opuserp.view.modulos.pedido.compras.ComprasView;
import com.digital.opuserp.view.modulos.pedido.naturezaOperacao.NaturezaOperacaoView;
import com.digital.opuserp.view.modulos.pedido.pedidoProduto.PedidoView;
import com.digital.opuserp.view.modulos.pedido.rma.RmaView;
import com.digital.opuserp.view.modulos.relatorio.Acesso.RelatorioAcessoView;
import com.digital.opuserp.view.modulos.relatorio.AcessoLog.RelatorioAcessoLogView;
import com.digital.opuserp.view.modulos.relatorio.Cliente.RelatorioClienteView;
import com.digital.opuserp.view.modulos.relatorio.Compras.RelatorioCompraView;
import com.digital.opuserp.view.modulos.relatorio.Consumo.RelatorioConsumoView;
import com.digital.opuserp.view.modulos.relatorio.ContasPagar.RelatorioContasPagarView;
import com.digital.opuserp.view.modulos.relatorio.ContasReceber.RelatorioContasReceberView;
import com.digital.opuserp.view.modulos.relatorio.Crm.RelatorioCrmView;
import com.digital.opuserp.view.modulos.relatorio.Pedido.RelatorioPedidoView;
import com.digital.opuserp.view.modulos.relatorio.Pesquisa.RelatorioPesquisaView;
import com.digital.opuserp.view.modulos.relatorio.Producao.RelatorioOspView;
import com.digital.opuserp.view.modulos.relatorio.Produto.RelatorioProdutoView;
import com.digital.opuserp.view.modulos.relatorio.Roterizacao.RelatorioOseRelRepInsView;
import com.digital.opuserp.view.modulos.relatorio.Roterizacao.RelatorioOseView;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.VerticalLayout;

public class DashboardView extends VerticalLayout{

	
	//Views Módulo de Cadastro	
	CepView cepView = new CepView(false);
	FornecedorView fornecedorView = new FornecedorView(false);
	TransportadoraView transportadoraView = new TransportadoraView(false);
	VeiculosView veiculoView = new VeiculosView(false);
	SetoresView setoresView = new SetoresView(false);
	ClientesView clientesView = new ClientesView(false);
	
	//View Módulo de Acesso
	PlanosView planosView = new PlanosView(false);
	ContratosView contratosView = new ContratosView(false);
	ConfigMaterialAcessoView configMaterialAcessoView = new ConfigMaterialAcessoView();
	ContratoAcessoView credenciaisView = new ContratoAcessoView(false);
	ConcentradorView concentradorView = new ConcentradorView(false);
	BaseView baseView = new BaseView(false);
	CaixaDeAtendimentoView caixaAtendimentoView = new CaixaDeAtendimentoView(false);
	CentralAssinantesView centralAssinantesView = new CentralAssinantesView(false);
	OltView oltView = new OltView(false);
	
	//Views Módulo Financeiro
	BaixaRapidaView baixaRapidaView = new BaixaRapidaView(false);
	ContasReceberView contasReceberView = new ContasReceberView(false);
	FormasPgtoView formaPgtoView = new FormasPgtoView(false);
	PreferenciaView preferenciaClienteView = new PreferenciaView(false);
	ContratoCobrancaView contratoCobview = new ContratoCobrancaView(false);
	HaverView haverView = new HaverView(false);
	ContasPagarView contasPagarView = new ContasPagarView(false);
	
	//View Ordens de Serviço
	ServicoView servicoView = new ServicoView(false);
	AssistenciaTecnicaView assistenciaTecnicaView = new AssistenciaTecnicaView(false);
	RoteirizacaoView roteirizacaoView = new RoteirizacaoView(false);
	OrdemProducaoView ordemProducaoView= new OrdemProducaoView(false, 0);
	PedidoServicoView pedidoServicoView = new PedidoServicoView(false);
	PlanoManutencaoView planoManutencaoView = new PlanoManutencaoView(false);
	ContratoManutencaoView contratoManutencaoView = new ContratoManutencaoView(false);
	MateriaisAlocadosView materiaisAlocadosView = new MateriaisAlocadosView(false);
	
	//View Estoque
	MarcaView marcaView = new MarcaView(false);	
	SerialView serialView = new SerialView(false);
	TabelaPrecoView tabelaPrecoView = new TabelaPrecoView(false);
	GrupoView grupoView = new GrupoView(false);
	ProdutoView produtoView = new ProdutoView(false);
	TipoItemView tipoItemView = new TipoItemView(false);
	GerenciamentoEstoqueView gerenciamentoEstoqueView = new GerenciamentoEstoqueView(false);
	FichaCegaView fichaCegaView = new FichaCegaView(false);
	
	//Views Relatorios
	RelatorioClienteView relClienteView = new RelatorioClienteView(false);
	RelatorioContasReceberView relaContasReceberView = new RelatorioContasReceberView(false);
	RelatorioContasPagarView relaContasPagarView = new RelatorioContasPagarView(false);
	RelatorioAcessoView relaAcessoView = new RelatorioAcessoView(false);
	RelatorioCrmView relaCrmView = new RelatorioCrmView(false);
	RelatorioOseView relaRoteView = new RelatorioOseView(false);
	RelatorioPedidoView relaPedidoView = new RelatorioPedidoView(false);
	RelatorioCompraView relaCompraView = new RelatorioCompraView(false);
	RelatorioConsumoView relaConsumoView = new RelatorioConsumoView(false);
	RelatorioPesquisaView relaPesquisaView = new RelatorioPesquisaView(false);
	RelatorioOspView relaOspView = new RelatorioOspView(false);
	RelatorioProdutoView relaProdutoView = new RelatorioProdutoView(false);
	RelatorioOseRelRepInsView relaRoteirizacaoRepInstView = new RelatorioOseRelRepInsView(false);
	RelatorioAcessoLogView relaAcessoLogView = new RelatorioAcessoLogView(false);
		
	//View Modulo Pedido
	ComprasView comprasView = new ComprasView(false);
	PedidoView pedidoView = new PedidoView(false);
	NaturezaOperacaoView naturezaView  = new NaturezaOperacaoView(false);
	RmaView rmaView = new RmaView(false);
	
	//View CRM Assuntos
	//AssuntosView assuntosView = new AssuntosView(false);
	ContatoView contatoView = new ContatoView(false);

	//ViewNfe
	NotasView notaView = new NotasView(false);
	com.digital.opuserp.view.modulos.nfe.modelo_55.NotasView notaView55 = new com.digital.opuserp.view.modulos.nfe.modelo_55.NotasView(false);
	ArquivosRemessaView arquivoRemessaView = new  ArquivosRemessaView(false);
	
	
	//Views Módulo de Configurações
	TabSheetConfigSistemaView configSistema = new TabSheetConfigSistemaView(false);
	TabSheetConfigCadastroView configCadastro = new TabSheetConfigCadastroView(false);
	
	UsuarioView userView = new UsuarioView(false);
	
	ConfigOrdemServicoView configOrdemView = new ConfigOrdemServicoView(false);
	ConfigCRMView configCrmView = new ConfigCRMView(false);
	ConfigFinanceiroView configFinanceiroView = new ConfigFinanceiroView(false);
	
   TabSheetAcessoView tableSheetAcesso = new TabSheetAcessoView(false);
   ConfigEstoqueView tableSheetEtoqueConfig = new ConfigEstoqueView(false);
   
   Help help = new Help();
   
   LogView logView = new LogView(false);
   
   ConfigNfeView configNfeView = new ConfigNfeView(false);
   ConfigCobrancaEmailView configCobrancaView = new ConfigCobrancaEmailView(false);
   
    private TabSheet ts_principal = new TabSheet();
    TabSheet.Tab t;

    public DashboardView(){
        setSizeFull();
        
    
        
        ts_principal.addListener(new TabSheet.SelectedTabChangeListener() {
			
			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
					
					cepView.buildShortcurEvents(event.getTabSheet().getSelectedTab());	
					fornecedorView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					transportadoraView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					veiculoView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					setoresView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					clientesView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					//configSistema.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					userView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					
					//Acesso
					concentradorView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					planosView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					contratosView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					credenciaisView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					baseView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					caixaAtendimentoView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					
					////Fiannceiro
					baixaRapidaView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					contasReceberView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					formaPgtoView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					contratoCobview.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					haverView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					contasPagarView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					//preferenciaClienteView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					
					assistenciaTecnicaView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					roteirizacaoView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					
					relClienteView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					relaContasReceberView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					relaContasPagarView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					relaOspView.buildShortcurEvents(event.getTabSheet().getSelectedTab());					
					relaAcessoView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					relaProdutoView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					relaAcessoLogView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					
					contatoView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					servicoView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					pedidoServicoView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					ordemProducaoView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
			
					marcaView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					tabelaPrecoView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					grupoView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					produtoView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					fichaCegaView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					gerenciamentoEstoqueView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					
					comprasView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					pedidoView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					naturezaView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					rmaView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					
					notaView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					notaView55.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					arquivoRemessaView.buildShortcurEvents(event.getTabSheet().getSelectedTab());
					
			}
		});
        
       

        addComponent(ts_principal);      
        ts_principal.setSizeFull();
        
		t = ts_principal.addTab(new PaginaInicialView(), "Página Inicial");
		if(OpusERP4UI.getUsuarioLogadoUI().getUsername().equals("marconi")){
			t = ts_principal.addTab(new PaginaInicialAdminErrorView(), "Logs de ERRO");        		
		}
       
        
        
    }  
    public void addTab(String s) throws Exception{
         
        Iterator<Component> i = ts_principal.getComponentIterator();
            while (i.hasNext()) {
                Component c = (Component) i.next();
                TabSheet.Tab tab = ts_principal.getTab(c);
                if (s.equals(tab.getCaption())) {
                    ts_principal.setSelectedTab(c);
                    return;
                }
            }
        
       
            
        if(s.equals("Página Inicial")){
        	ts_principal.setSelectedTab(0);
        }
        
        //Cadastros
        else if(s.equals("Clientes")){
        	clientesView = new ClientesView(true);        	 
        	t = ts_principal.addTab(clientesView, s);
        	clientesView.setCodSubModulo(SubModuloDAO.findToId(s));        	
        	
        	OpusERP4UI.getCurrent().getPage().setUriFragment("Clientes");
        }else if(s.equals("CEPs")){
        	cepView = new CepView(true);
        	t = ts_principal.addTab(cepView, s);
        	cepView.setCodSubModulo(SubModuloDAO.findToId(s));
        	
        	OpusERP4UI.getCurrent().getPage().setUriFragment("Ceps");
        }else if(s.equals("Transportadoras")){
        	transportadoraView = new TransportadoraView(true);
        	t = ts_principal.addTab(transportadoraView, s);
        	transportadoraView.setCodSubModulo(SubModuloDAO.findToId(s));
        	
        	OpusERP4UI.getCurrent().getPage().setUriFragment("Transportadoras");
        }else if(s.equals("Setores")){
        	setoresView = new SetoresView(true);
        	t = ts_principal.addTab(setoresView, s);
        	setoresView.setCodSubModulo(SubModuloDAO.findToId(s));
        	
        	OpusERP4UI.getCurrent().getPage().setUriFragment("Setores");
        }else if(s.equals("Ajuda")){
        	help = new Help();
        	t = ts_principal.addTab(help, s);
        	setoresView.setCodSubModulo(SubModuloDAO.findToId(s));
        	
        	OpusERP4UI.getCurrent().getPage().setUriFragment("Ajuda");
        }else if(s.equals("Fornecedores")){
        	fornecedorView = new FornecedorView(true);
        	t = ts_principal.addTab(fornecedorView, s);
        	fornecedorView.setCodSubModulo(SubModuloDAO.findToId(s));
        	
        	OpusERP4UI.getCurrent().getPage().setUriFragment("Fornecedores");
        }else if(s.equals("Veiculos")){
        	veiculoView = new VeiculosView(true);
        	t = ts_principal.addTab(veiculoView, s);
        	veiculoView.setCodSubModulo(SubModuloDAO.findToId(s));
        	
        	OpusERP4UI.getCurrent().getPage().setUriFragment("Veiculos");
        }
        
        //Acesso
        else if(s.equals("Concentradores")){        	
        	concentradorView = new ConcentradorView(true);
        	t = ts_principal.addTab(concentradorView, s);     
        	concentradorView.setCodSubModulo(SubModuloDAO.findToId(s));
        	
        	OpusERP4UI.getCurrent().getPage().setUriFragment("Concentradores");
        }else if(s.equals("Bases")){
        	baseView = new BaseView(true);
        	t = ts_principal.addTab(baseView, s);
        	baseView.setCodSubModulo(SubModuloDAO.findToId(s));
        	
        	OpusERP4UI.getCurrent().getPage().setUriFragment("Bases");
        }else if(s.equals("Planos de Acesso")){
        	planosView = new PlanosView(true);
        	t = ts_principal.addTab(planosView, s);
        	planosView.setCodSubModulo(SubModuloDAO.findToId(s));
        	
        	OpusERP4UI.getCurrent().getPage().setUriFragment("PlanosAcesso");
        }else if(s.equals("Credenciais de Acesso")){
        	credenciaisView = new ContratoAcessoView(true);
        	t = ts_principal.addTab(credenciaisView, s);
        	credenciaisView.setCodSubModulo(SubModuloDAO.findToId(s));
        	
        	OpusERP4UI.getCurrent().getPage().setUriFragment("CredenciaisAcesso");
        }else if(s.equals("Contratos de Acesso")){
        	credenciaisView = new ContratoAcessoView(true);
        	t = ts_principal.addTab(credenciaisView, s);
        	credenciaisView.setCodSubModulo(SubModuloDAO.findToId(s));
        	
        	OpusERP4UI.getCurrent().getPage().setUriFragment("ContratosAcesso");
        }else if(s.equals("Central de Assinantes")){
        	centralAssinantesView = new CentralAssinantesView(true);
        	t = ts_principal.addTab(centralAssinantesView, s);
        	centralAssinantesView.setCodSubModulo(SubModuloDAO.findToId(s));
        	
        	OpusERP4UI.getCurrent().getPage().setUriFragment("CentralAssinantes");
        }
        
        else if(s.equals("OLTs")){
        	oltView = new OltView(true);
        	t = ts_principal.addTab(oltView, s);
        	oltView.setCodSubModulo(SubModuloDAO.findToId(s));
        	
        	OpusERP4UI.getCurrent().getPage().setUriFragment("OLTs");
        }
        
        else if(s.equals("Relatórios de Clientes")){
        	relClienteView = new RelatorioClienteView(true);
        	t = ts_principal.addTab(relClienteView, s);
        	relClienteView.setCodSubModulo(SubModuloDAO.findToId(s));
        	
        	OpusERP4UI.getCurrent().getPage().setUriFragment("RelatorioClientes");
        }else if(s.equals("Relatórios de Produto")){
        	relaProdutoView = new RelatorioProdutoView(true);
        	t = ts_principal.addTab(relaProdutoView, s);
        	relaProdutoView.setCodSubModulo(SubModuloDAO.findToId(s));
        	
        	OpusERP4UI.getCurrent().getPage().setUriFragment("RelatorioProdutos");
        } else if(s.equals("Relatórios de Contas à Receber")){
        	relaContasReceberView = new RelatorioContasReceberView(true);
        	t = ts_principal.addTab(relaContasReceberView, s);
        	relaContasReceberView.setCodSubModulo(SubModuloDAO.findToId(s));
        	
        	OpusERP4UI.getCurrent().getPage().setUriFragment("RelatorioContasReceber");
        	
        } else if(s.equals("Relatórios de Acesso")){
        	relaAcessoView = new RelatorioAcessoView(true);
        	t = ts_principal.addTab(relaAcessoView, s);
        	relaAcessoView.setCodSubModulo(SubModuloDAO.findToId(s));
        	
        } else if(s.equals("Relatórios de CRM")){
        	relaCrmView = new RelatorioCrmView(true);
        	t = ts_principal.addTab(relaCrmView, s);
        	relaCrmView.setCodSubModulo(SubModuloDAO.findToId(s));
        	
        } else if(s.equals("Relatórios de Roteirização")){
        	relaRoteView = new RelatorioOseView(true);
        	t = ts_principal.addTab(relaRoteView, s);
        	relaRoteView.setCodSubModulo(SubModuloDAO.findToId(s));
        	
        }else if(s.equals("Relatórios de Roteirização Rep/Inst")){
        	relaRoteirizacaoRepInstView = new RelatorioOseRelRepInsView(true);
        	t = ts_principal.addTab(relaRoteirizacaoRepInstView, s);
        	relaRoteirizacaoRepInstView.setCodSubModulo(SubModuloDAO.findToId(s));
        	
        } else if(s.equals("Relatórios de Pedidos")){
        	relaPedidoView = new RelatorioPedidoView(true);
        	t = ts_principal.addTab(relaPedidoView, s);
        	relaPedidoView.setCodSubModulo(SubModuloDAO.findToId(s));
        	
        } else if(s.equals("Relatórios de Compras")){
        	relaCompraView = new RelatorioCompraView(true);
        	t = ts_principal.addTab(relaCompraView, s);
        	relaCompraView.setCodSubModulo(SubModuloDAO.findToId(s));
        	
        } else if(s.equals("Relatórios de Consumo")){
        	relaConsumoView = new RelatorioConsumoView(true);
        	t = ts_principal.addTab(relaConsumoView, s);
        	relaConsumoView.setCodSubModulo(SubModuloDAO.findToId(s));
        	
        }else if(s.equals("Caixas de Atendimento")){
        	caixaAtendimentoView = new CaixaDeAtendimentoView(true);
        	t = ts_principal.addTab(caixaAtendimentoView, s);
        	caixaAtendimentoView.setCodSubModulo(SubModuloDAO.findToId(s));
        
	    }else if(s.equals("Relatórios de Contas à Pagar")){
	    	relaContasPagarView = new RelatorioContasPagarView(true);
	    	t = ts_principal.addTab(relaContasPagarView, s);
	    	relaContasPagarView.setCodSubModulo(SubModuloDAO.findToId(s));
	    	
	    }else if(s.equals("Relatórios de Pesquisa")){
	    	relaPesquisaView = new RelatorioPesquisaView(true);
	    	t = ts_principal.addTab(relaPesquisaView, s);
	    	relaPesquisaView.setCodSubModulo(SubModuloDAO.findToId(s));

	    }else if(s.equals("Relatórios de Produção")){
	    	relaOspView = new RelatorioOspView(true);
	    	t = ts_principal.addTab(relaOspView, s);
	    	relaOspView.setCodSubModulo(SubModuloDAO.findToId(s));

	    }else if(s.equals("Relatórios de Acesso Log")){
	    	relaAcessoLogView = new RelatorioAcessoLogView(true);
	    	t = ts_principal.addTab(relaAcessoLogView, s);
	    	relaAcessoLogView.setCodSubModulo(SubModuloDAO.findToId(s));

	    }
        
        
        
        
        //Financeiro      
        else if(s.equals("Baixa Rapida")){
        	baixaRapidaView = new BaixaRapidaView(true);
        	t = ts_principal.addTab(  baixaRapidaView, s);
        	baixaRapidaView.setCodSubModulo(SubModuloDAO.findToId(s));
	    }else if(s.equals("Contas a Receber")){
        	contasReceberView = new ContasReceberView(true);
        	t = ts_principal.addTab(contasReceberView, s);
        	contasReceberView.setCodSubModulo(SubModuloDAO.findToId(s));
	    }else if(s.equals("Formas de Pgto.")){
	    	formaPgtoView = new FormasPgtoView(true);
        	t = ts_principal.addTab(formaPgtoView, s);        
        	formaPgtoView.setCodSubModulo(SubModuloDAO.findToId(s));
	    }else if(s.equals("Preferências")){
	    	preferenciaClienteView = new PreferenciaView(true);
        	t = ts_principal.addTab(preferenciaClienteView, s);        
        	preferenciaClienteView.setCodSubModulo(SubModuloDAO.findToId(s));
	    }else if(s.equals("Contratos de Cobrança")){
	    	contratoCobview = new ContratoCobrancaView(true);
        	t = ts_principal.addTab(contratoCobview, s);        
        	contratoCobview.setCodSubModulo(SubModuloDAO.findToId(s));
	    }else if(s.equals("Haver")){
	    	haverView = new HaverView(true);
        	t = ts_principal.addTab(haverView, s);        
        	haverView.setCodSubModulo(SubModuloDAO.findToId(s));
	    }else if(s.equals("Contas a Pagar")){
	    	contasPagarView = new ContasPagarView(true);
	    	t = ts_principal.addTab(contasPagarView, s);        
	    	contasPagarView.setCodSubModulo(SubModuloDAO.findToId(s));
	    }
        
                
        //Ordens de Serviço
        else if(s.equals("Servicos")){
        	servicoView = new ServicoView(true);
        	t = ts_principal.addTab(servicoView, s);
        	servicoView.setCodSubModulo(SubModuloDAO.findToId(s));
	    }else if(s.equals("Assistência Técnica")){
        	assistenciaTecnicaView = new AssistenciaTecnicaView(true);
        	t = ts_principal.addTab(assistenciaTecnicaView, s);
        	assistenciaTecnicaView.setCodSubModulo(SubModuloDAO.findToId(s));
	    }else if(s.equals("Roteirização")){
        	roteirizacaoView = new RoteirizacaoView(true);
        	t = ts_principal.addTab(roteirizacaoView, s);
        	roteirizacaoView.setCodSubModulo(SubModuloDAO.findToId(s));
	    }else if(s.equals("Produção")){
        	ordemProducaoView = new OrdemProducaoView(true,SubModuloDAO.findToId(s));
        	t = ts_principal.addTab(ordemProducaoView, s);
        	ordemProducaoView.setCodSubModulo(SubModuloDAO.findToId(s));
	    }else if(s.equals("Pedido de Serviço")){
        	pedidoServicoView = new PedidoServicoView(true);
        	t = ts_principal.addTab(pedidoServicoView, s);
        	pedidoServicoView.setCodSubModulo(SubModuloDAO.findToId(s));
	    }else if(s.equals("Planos de Manutenção")){
        	planoManutencaoView = new PlanoManutencaoView(true);
        	t = ts_principal.addTab(planoManutencaoView, s);
        	planoManutencaoView.setCodSubModulo(SubModuloDAO.findToId(s));
	    }else if(s.equals("Contratos de Manutenção")){
        	contratoManutencaoView = new ContratoManutencaoView(true);
        	t = ts_principal.addTab(contratoManutencaoView, s);
        	contratoManutencaoView.setCodSubModulo(SubModuloDAO.findToId(s));
	    }else if(s.equals("Materiais Alocados")){
        	materiaisAlocadosView = new MateriaisAlocadosView(true);
        	t = ts_principal.addTab(materiaisAlocadosView, s);
        	materiaisAlocadosView.setCodSubModulo(SubModuloDAO.findToId(s));
	    }
        
        //Estoque
        else if(s.equals("Seriais")){
        	serialView = new SerialView(true);
        	t = ts_principal.addTab(serialView, s);
        	serialView.setCodSubModulo(SubModuloDAO.findToId(s));
	    } else if(s.equals("Marcas")){
        	marcaView = new MarcaView(true);
        	t = ts_principal.addTab(marcaView, s);
        	marcaView.setCodSubModulo(SubModuloDAO.findToId(s));
	    } else if(s.equals("Tabelas de Preço")){
        	tabelaPrecoView = new TabelaPrecoView(true);
        	t = ts_principal.addTab(tabelaPrecoView, s);
        	tabelaPrecoView.setCodSubModulo(SubModuloDAO.findToId(s));
	    } else if(s.equals("Grupos de Produto")){
        	grupoView = new GrupoView(true);
        	t = ts_principal.addTab(grupoView, s);
        	grupoView.setCodSubModulo(SubModuloDAO.findToId(s));
	    } else if(s.equals("Produtos")){
        	produtoView = new ProdutoView(true);
        	t = ts_principal.addTab(produtoView, s);
        	produtoView.setCodSubModulo(SubModuloDAO.findToId(s));
	    }else if(s.equals("Ficha Cega")){
        	fichaCegaView = new FichaCegaView(true);
        	t = ts_principal.addTab(fichaCegaView, s);
        	fichaCegaView.setCodSubModulo(SubModuloDAO.findToId(s));
	    }else if(s.equals("Tipo Item")){
        	tipoItemView = new TipoItemView(true);
        	t = ts_principal.addTab(tipoItemView, s);
        	tipoItemView.setCodSubModulo(SubModuloDAO.findToId(s));
	    }else if(s.equals("Gerenciamento Estoque")){
        	gerenciamentoEstoqueView = new GerenciamentoEstoqueView(true);
        	t = ts_principal.addTab(gerenciamentoEstoqueView, s);
        	gerenciamentoEstoqueView.setCodSubModulo(SubModuloDAO.findToId(s));
	    }
        
        
        //Pedido
	    else if(s.equals("Compras")){
        	comprasView = new ComprasView(true);
        	t = ts_principal.addTab(comprasView, s);
        	comprasView.setCodSubModulo(SubModuloDAO.findToId(s));
	    }else if(s.equals("Pré-venda")){
        	pedidoView = new PedidoView(true);
        	t = ts_principal.addTab(pedidoView, s);
        	pedidoView.setCodSubModulo(SubModuloDAO.findToId(s));
	    }else if(s.equals("Natureza da Operacao")){
        	naturezaView = new NaturezaOperacaoView(true);
        	t = ts_principal.addTab(naturezaView, s);
        	naturezaView.setCodSubModulo(SubModuloDAO.findToId(s));
	    }else if(s.equals("RMA")){
        	rmaView = new RmaView(true);
        	t = ts_principal.addTab(rmaView, s);
        	rmaView.setCodSubModulo(SubModuloDAO.findToId(s));
	    }
        
        
        //Configurações
        else if(s.equals("Config-Acesso")){
        	tableSheetAcesso = new TabSheetAcessoView(true);
        	t = ts_principal.addTab(tableSheetAcesso, s);     	
        }
        else if(s.equals("Config-Financeiro")){
        	configFinanceiroView  = new ConfigFinanceiroView(true);
        	t = ts_principal.addTab(configFinanceiroView, s);
        	//controleView.setCodSubModulo(SubModuloDAO.findToId(s)); 
        }
        else if(s.equals("Config-Sistema")){
        	configSistema = new TabSheetConfigSistemaView(true);
        	t = ts_principal.addTab(configSistema, s);
        }
        else if(s.equals("Config-Cadastro")){
        	configCadastro = new TabSheetConfigCadastroView(true);
        	t = ts_principal.addTab(configCadastro, s);
        }else if(s.equals("Contatos")){
        	contatoView = new ContatoView(true);
        	t = ts_principal.addTab(contatoView, s);
        	contatoView.setCodSubModulo(SubModuloDAO.findToId(s));
        }else if(s.equals("Config-Ordens de Serviço")){
        	configOrdemView = new ConfigOrdemServicoView(true);
        	t = ts_principal.addTab(configOrdemView, s);
        	configOrdemView.setCodSubModulo(SubModuloDAO.findToId(s));
        }else if(s.equals("Config-Crm")){
        	configCrmView = new ConfigCRMView(true);
        	t = ts_principal.addTab(configCrmView, s);
        	//configOrdemView.setCodSubModulo(SubModuloDAO.findToId(s));
        }else if(s.equals("Logs")){
        	logView = new LogView(true);
        	t = ts_principal.addTab(logView, s);
        	logView.setCodSubModulo(SubModuloDAO.findToId(s));
        }else if(s.equals("Config-Estoque")){
        	tableSheetEtoqueConfig = new ConfigEstoqueView(true);
        	t = ts_principal.addTab(tableSheetEtoqueConfig, s);
        	logView.setCodSubModulo(SubModuloDAO.findToId(s));
        }else if(s.equals("Config-Nfe")){
        	configNfeView = new ConfigNfeView(true);
        	t = ts_principal.addTab(configNfeView, s);
        	logView.setCodSubModulo(SubModuloDAO.findToId(s));
        }else if(s.equals("Config-Cobrança por E-Mail")){
        	configCobrancaView = new ConfigCobrancaEmailView(true);
        	t = ts_principal.addTab(configCobrancaView, s);
        	logView.setCodSubModulo(SubModuloDAO.findToId(s));
        }
        
        //Nfe
        else if(s.equals("NFe Mod-21")){
        	notaView = new NotasView(true);
        	t = ts_principal.addTab(notaView, s);
        	notaView.setCodSubModulo(SubModuloDAO.findToId(s));
        } else if(s.equals("NFe Mod-55") ){
        	notaView55 = new com.digital.opuserp.view.modulos.nfe.modelo_55.NotasView(true);
        	t = ts_principal.addTab(notaView55, s);
        	notaView55.setCodSubModulo(SubModuloDAO.findToId(s));
        }else if(s.equals("Arquivo Remessa")){
        	arquivoRemessaView = new ArquivosRemessaView(true);
        	t = ts_principal.addTab(arquivoRemessaView, s);
        	arquivoRemessaView.setCodSubModulo(SubModuloDAO.findToId(s));
        }
            
       ts_principal.setSelectedTab(t);               
       t.setClosable(true);
    }
	
    
   
}
